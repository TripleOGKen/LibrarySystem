package student.inti.librarysystem.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import student.inti.librarysystem.data.entity.RoomBooking;
import java.util.Date;
import java.util.List;

@Dao
public interface RoomBookingDao {
    @Insert
    void insert(RoomBooking booking);

    @Update
    void update(RoomBooking booking);

    @Delete
    void delete(RoomBooking booking);

    @Query("SELECT * FROM room_bookings")
    List<RoomBooking> getAllBookings();

    @Query("SELECT * FROM room_bookings WHERE id = :id")
    LiveData<RoomBooking> getBooking(long id);

    @Query("SELECT * FROM room_bookings WHERE roomNumber = :roomNumber AND " +
            "((startTime BETWEEN :startTime AND :endTime) OR " +
            "(endTime BETWEEN :startTime AND :endTime))")
    List<RoomBooking> getConflictingBookings(int roomNumber, Date startTime, Date endTime);

    @Query("SELECT * FROM room_bookings WHERE bookingStudentId = :studentId")
    LiveData<List<RoomBooking>> getStudentBookings(String studentId);

    @Query("SELECT * FROM room_bookings WHERE roomNumber = :roomNumber AND startTime >= :today")
    LiveData<List<RoomBooking>> getRoomBookings(int roomNumber, Date today);

    @Query("DELETE FROM room_bookings WHERE endTime < :date")
    void deletePastBookings(Date date);
}