// ExamPapersAdapter.java
package student.inti.librarysystem.ui.exams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.R;
import student.inti.librarysystem.data.entity.ExamPaper;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class ExamPapersAdapter extends RecyclerView.Adapter<ExamPapersAdapter.ExamPaperViewHolder> {
    private List<ExamPaper> examPapers = new ArrayList<>();
    private final OnPaperClickListener listener;

    public interface OnPaperClickListener {
        void onDownloadClick(ExamPaper paper);
        void onViewClick(ExamPaper paper);
    }

    public ExamPapersAdapter(OnPaperClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExamPaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_paper, parent, false);
        return new ExamPaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamPaperViewHolder holder, int position) {
        ExamPaper paper = examPapers.get(position);
        holder.bind(paper, listener);
    }

    @Override
    public int getItemCount() {
        return examPapers.size();
    }

    public void setExamPapers(List<ExamPaper> examPapers) {
        this.examPapers = examPapers;
        notifyDataSetChanged();
    }

    static class ExamPaperViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectNameText;
        private final TextView subjectCodeText;
        private final TextView yearSemesterText;
        private final TextView fileSizeText;
        private final MaterialButton viewButton;
        private final MaterialButton downloadButton;

        public ExamPaperViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            subjectCodeText = itemView.findViewById(R.id.subjectCodeText);
            yearSemesterText = itemView.findViewById(R.id.yearSemesterText);
            fileSizeText = itemView.findViewById(R.id.fileSizeText);
            viewButton = itemView.findViewById(R.id.viewButton);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }

        public void bind(ExamPaper paper, OnPaperClickListener listener) {
            subjectNameText.setText(paper.getSubjectName());
            subjectCodeText.setText(paper.getSubjectCode());
            yearSemesterText.setText(String.format("Year: %d, Semester: %d", paper.getYear(), paper.getSemester()));
            fileSizeText.setText(formatFileSize(paper.getFileSize()));
            viewButton.setOnClickListener(v -> listener.onViewClick(paper));
            downloadButton.setOnClickListener(v -> listener.onDownloadClick(paper));
        }

        private String formatFileSize(long size) {
            if (size < 1024) return size + " B";
            int exp = (int) (Math.log(size) / Math.log(1024));
            String pre = "KMGTPE".charAt(exp-1) + "";
            return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
        }
    }
}