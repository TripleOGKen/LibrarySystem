package student.inti.librarysystem.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(
        tableName = "room_bookings",
        foreignKeys = @ForeignKey(
                entity = Student.class,
                parentColumns = "studentId",
                childColumns = "bookingStudentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("bookingStudentId")} // Add index for foreign key
)
public class RoomBooking {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String bookingStudentId;
    private int roomNumber;
    private Date startTime;
    private Date endTime;
    private String participantNames;
    private String participantIds;

    public RoomBooking(String bookingStudentId, int roomNumber, Date startTime,
                       Date endTime, String participantNames, String participantIds) {
        this.bookingStudentId = bookingStudentId;
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantNames = participantNames;
        this.participantIds = participantIds;
    }

    // Existing getters and setters remain the same
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getBookingStudentId() { return bookingStudentId; }
    public void setBookingStudentId(String bookingStudentId) {
        this.bookingStudentId = bookingStudentId;
    }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public String getParticipantNames() { return participantNames; }
    public void setParticipantNames(String participantNames) {
        this.participantNames = participantNames;
    }

    public String getParticipantIds() { return participantIds; }
    public void setParticipantIds(String participantIds) {
        this.participantIds = participantIds;
    }
}