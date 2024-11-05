package student.inti.librarysystem.ui.bookloans;

import android.content.Context;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import student.inti.librarysystem.data.entity.BookLoan;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class BookLoanService {
    public static void scheduleDueReminder(Context context, BookLoan loan, String bookTitle) {
        // Schedule reminder for 1 day before due date
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(loan.getReturnDate());
        dueDate.add(Calendar.DAY_OF_MONTH, -1);

        long delayMillis = dueDate.getTimeInMillis() - System.currentTimeMillis();
        if (delayMillis > 0) {
            Data notificationData = new Data.Builder()
                    .putString("title", "Book Due Tomorrow")
                    .putString("message", "Please return the book tomorrow")
                    .putString("bookTitle", bookTitle)
                    .build();

            OneTimeWorkRequest reminderWork = new OneTimeWorkRequest.Builder(BookLoanNotificationWorker.class)
                    .setInputData(notificationData)
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .build();

            WorkManager.getInstance(context).enqueue(reminderWork);
        }
    }

    public static void scheduleReturnedBookReminder(Context context, String bookTitle) {
        Data notificationData = new Data.Builder()
                .putString("title", "Book Returned")
                .putString("message", "Thank you for returning")
                .putString("bookTitle", bookTitle)
                .build();

        OneTimeWorkRequest returnedWork = new OneTimeWorkRequest.Builder(BookLoanNotificationWorker.class)
                .setInputData(notificationData)
                .build();

        WorkManager.getInstance(context).enqueue(returnedWork);
    }
}