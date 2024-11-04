package student.inti.librarysystem.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import student.inti.librarysystem.util.DateConverter;
import java.util.Date;

@Entity(tableName = "room_bookings")
@TypeConverters(DateConverter.class)
public class RoomBooking {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String bookingId; // Firebase document ID
    private String roomNumber;
    private Date startTime;
    private Date endTime;
    private String bookingStudentId;
    private String participantsNames;
    private String participantsIds;
    private String status;

    // Constructors
    public RoomBooking() {}

    public RoomBooking(String roomNumber, Date startTime, Date endTime,
                       String bookingStudentId, String participantsNames,
                       String participantsIds, String status) {
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStudentId = bookingStudentId;
        this.participantsNames = participantsNames;
        this.participantsIds = participantsIds;
        this.status = status;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getBookingStudentId() {
        return bookingStudentId;
    }

    public void setBookingStudentId(String bookingStudentId) {
        this.bookingStudentId = bookingStudentId;
    }

    public String getParticipantsNames() {
        return participantsNames;
    }

    public void setParticipantsNames(String participantsNames) {
        this.participantsNames = participantsNames;
    }

    public String getParticipantsIds() {
        return participantsIds;
    }

    public void setParticipantsIds(String participantsIds) {
        this.participantsIds = participantsIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Convert from Firebase model to Room entity
    public static RoomBooking fromFirebaseModel(student.inti.librarysystem.RoomBooking firebaseBooking) {
        RoomBooking roomBooking = new RoomBooking(
                firebaseBooking.getRoomNumber(),
                firebaseBooking.getStartTime(),
                firebaseBooking.getEndTime(),
                firebaseBooking.getBookingStudentId(),
                firebaseBooking.getParticipantsNames(),
                firebaseBooking.getParticipantsIds(),
                firebaseBooking.getStatus()
        );
        roomBooking.setBookingId(firebaseBooking.getBookingId());
        return roomBooking;
    }

    // Convert to Firebase model
    public student.inti.librarysystem.RoomBooking toFirebaseModel() {
        student.inti.librarysystem.RoomBooking firebaseBooking = new student.inti.librarysystem.RoomBooking(
                roomNumber,
                startTime,
                endTime,
                bookingStudentId,
                participantsNames,
                participantsIds,
                status
        );
        firebaseBooking.setBookingId(bookingId);
        return firebaseBooking;
    }
}