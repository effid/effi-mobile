package com.efficom.efid.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.efficom.efid.R
import com.efficom.efid.data.model.User
import com.efficom.efid.data.model.request.LoginRequest
import com.efficom.efid.data.model.sealedClass.*
import com.efficom.efid.databinding.FragmentLoginBinding
import com.efficom.efid.ui.activity.MainActivity
import com.efficom.efid.viewmodel.LoginViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment: BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginRequest: LoginRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(LoginViewModel::class.java)

        loginRequest = LoginRequest()
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,
            R.layout.fragment_login, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.login = loginRequest
        binding.executePendingBindings()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        setupClickOutside(login_layout)
        login_title.setTypeface(null, Typeface.BOLD)

        viewModel.canConnectUser.observe(viewLifecycleOwner, Observer {
            if (saveUser(it)){
                navigateToMainActivity()
            }
        })
        viewModel.waitingVisibility.observe(viewLifecycleOwner, Observer {
            changeSpinnerVisibility()
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            changeSpinnerVisibility()
            if (isInternetAvailable()){
                displayErrorMessage(it)
            }else {
                displayErrorMessage(NoInternetConnection)
            }

        })

        login_forgot_password.setOnClickListener {
            navigateToForgotPassword()
        }
    }

    private fun saveUser(user: User): Boolean{
        val editor = sharedPreferences.edit()
        editor.putString("user", Gson().toJson(user))
        editor.putBoolean("isConnected", login_checkBox.isChecked)

        return editor.commit()
    }

    private fun navigateToMainActivity(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToRegister(){
        findNavController().navigate(LoginFragmentDirections.loginToRegister())
    }

    private fun navigateToForgotPassword(){
        findNavController().navigate(LoginFragmentDirections.loginToForgotPassword())
    }

    private fun changeSpinnerVisibility(){

        if (login_spinner.visibility == View.GONE){
            login_spinner.visibility = View.VISIBLE
            input_email.isEnabled = false
            input_password.isEnabled = false
            login_connect_btn.isEnabled = false
            login_forgot_password.isEnabled = false
            input_email.alpha = 0.75F
            input_password.alpha = 0.75F
            login_connect_btn.alpha = 0.75F
            login_forgot_password.alpha = 0.75F
        }
        else {
            login_spinner.visibility = View.GONE
            input_email.isEnabled = true
            input_password.isEnabled = true
            login_connect_btn.isEnabled = true
            login_forgot_password.isEnabled = true
            input_email.alpha = 1F
            input_password.alpha = 1F
            login_connect_btn.alpha = 1F
            login_forgot_password.alpha = 1F
        }
    }

    private fun displayErrorMessage(error: AuthApiReturn){
        val message: String = when(error){
            is LoginIsWrong -> resources.getString(R.string.login_wrong_login)
            is LoginEmailInvalid -> resources.getString(R.string.login_wrong_email)
            is LoginEmptyField -> resources.getString(R.string.login_empty_field)
            is NoInternetConnection -> "Veuillez vérifier votre connexion Internet"
            else -> ""
        }
        displayToast(message, context)
    }
}