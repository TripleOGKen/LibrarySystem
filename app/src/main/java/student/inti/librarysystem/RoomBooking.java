package student.inti.librarysystem;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;
import java.util.Objects;

public class RoomBooking {
    @DocumentId
    private String id; // Firestore auto-ID

    @PropertyName("bookingStudentId")
    private String bookingStudentId;

    @PropertyName("endTime")
    private Date endTime;

    @PropertyName("participantsIds")
    private String participantsIds;

    @PropertyName("participantsNames")
    private String participantsNames;

    @PropertyName("roomNumber")
    private String roomNumber;

    @PropertyName("startTime")
    private Date startTime;

    @PropertyName("status")
    private String status;

    // Required empty constructor for Firestore
    public RoomBooking() {}

    public RoomBooking(String bookingStudentId, Date endTime, String participantsIds,
                       String participantsNames, String roomNumber, Date startTime,
                       String status) {
        this.bookingStudentId = bookingStudentId;
        this.endTime = endTime;
        this.participantsIds = participantsIds;
        this.participantsNames = participantsNames;
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.status = status;
    }

    // Getters and Setters with PropertyName annotations to match Firestore fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("bookingStudentId")
    public String getBookingStudentId() {
        return bookingStudentId;
    }

    @PropertyName("bookingStudentId")
    public void setBookingStudentId(String bookingStudentId) {
        this.bookingStudentId = bookingStudentId;
    }

    @PropertyName("endTime")
    public Date getEndTime() {
        return endTime;
    }

    @PropertyName("endTime")
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @PropertyName("participantsIds")
    public String getParticipantsIds() {
        return participantsIds;
    }

    @PropertyName("participantsIds")
    public void setParticipantsIds(String participantsIds) {
        this.participantsIds = participantsIds;
    }

    @PropertyName("participantsNames")
    public String getParticipantsNames() {
        return participantsNames;
    }

    @PropertyName("participantsNames")
    public void setParticipantsNames(String participantsNames) {
        this.participantsNames = participantsNames;
    }

    @PropertyName("roomNumber")
    public String getRoomNumber() {
        return roomNumber;
    }

    @PropertyName("roomNumber")
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @PropertyName("startTime")
    public Date getStartTime() {
        return startTime;
    }

    @PropertyName("startTime")
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomBooking that = (RoomBooking) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookingStudentId, that.bookingStudentId) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(participantsIds, that.participantsIds) &&
                Objects.equals(participantsNames, that.participantsNames) &&
                Objects.equals(roomNumber, that.roomNumber) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookingStudentId, endTime, participantsIds,
                participantsNames, roomNumber, startTime, status);
    }

    @Override
    public String toString() {
        return "RoomBooking{" +
                "id='" + id + '\'' +
                ", bookingStudentId='" + bookingStudentId + '\'' +
                ", endTime=" + endTime +
                ", participantsIds='" + participantsIds + '\'' +
                ", participantsNames='" + participantsNames + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", startTime=" + startTime +
                ", status='" + status + '\'' +
                '}';
    }
}