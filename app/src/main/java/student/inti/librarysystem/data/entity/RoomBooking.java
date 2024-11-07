package student.inti.librarysystem.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.TypeConverters;
import student.inti.librarysystem.util.DateConverter;
import java.util.Date;

@Entity(tableName = "room_bookings")
@TypeConverters(DateConverter.class)
public class RoomBooking {
    @PrimaryKey
    @NonNull
    private String id;
    private String bookingStudentId;
    private Date endTime;
    private String participantsIds;
    private String participantsNames;
    private long roomNumber;
    private Date startTime;
    private String status;

    // Required empty constructor for Room
    public RoomBooking() {}

    // Constructor for normal use
    @Ignore
    public RoomBooking(String bookingStudentId, Date endTime,
                       String participantsIds, String participantsNames,
                       long roomNumber, Date startTime, String status) {
        this.bookingStudentId = bookingStudentId;
        this.endTime = endTime;
        this.participantsIds = participantsIds;
        this.participantsNames = participantsNames;
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.status = status;
    }

    // Getters and Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getBookingStudentId() {
        return bookingStudentId;
    }

    public void setBookingStudentId(String bookingStudentId) {
        this.bookingStudentId = bookingStudentId;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getParticipantsIds() {
        return participantsIds;
    }

    public void setParticipantsIds(String participantsIds) {
        this.participantsIds = participantsIds;
    }

    public String getParticipantsNames() {
        return participantsNames;
    }

    public void setParticipantsNames(String participantsNames) {
        this.participantsNames = participantsNames;
    }

    public long getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(long roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Optionally add equals() and hashCode() methods if needed
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomBooking that = (RoomBooking) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}