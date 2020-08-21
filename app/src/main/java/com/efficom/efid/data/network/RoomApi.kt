package com.efficom.efid.data.network

import com.efficom.efid.data.model.Reservation
import com.efficom.efid.data.model.ReservedRoom
import com.efficom.efid.data.model.Room
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RoomApi {

    @GET("salles/dispoNow")
    suspend fun getFreeRoom(): Response<List<Room>>

    @GET("reservations/user/{user_id}")
    suspend fun getOldReserve(@Path("user_id")user_id: String): Response<List<Reservation>>

    @GET("salles/reservees/{date}")
    suspend fun getFreeRoomByDate(@Path("date") date: String): Response<List<ReservedRoom>>
}