package student.inti.librarysystem.ui.exams;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.R;
import student.inti.librarysystem.data.entity.ExamPaper;
import student.inti.librarysystem.viewmodel.ExamPaperViewModel;
import student.inti.librarysystem.util.FirebaseManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.content.Intent;
import student.inti.librarysystem.ui.pdf.PdfViewerActivity;
import student.inti.librarysystem.util.DriveUrlConverter;

public class ExamPapersFragment extends Fragment implements ExamPapersAdapter.OnPaperClickListener {
    private ExamPaperViewModel viewModel;
    private ExamPapersAdapter adapter;
    private View loadingView;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        return inflater.inflate(R.layout.fragment_exam_papers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if user is logged in
        if (!FirebaseManager.getInstance().isUserLoggedIn()) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ExamPaperViewModel.class);

        // Initialize views
        RecyclerView recyclerView = view.findViewById(R.id.examPapersRecyclerView);
        TextInputEditText subjectCodeEdit = view.findViewById(R.id.subjectCodeEdit);
        TextInputEditText yearEdit = view.findViewById(R.id.yearEdit);
        TextInputEditText semesterEdit = view.findViewById(R.id.semesterEdit);
        View applyFilterButton = view.findViewById(R.id.applyFilterButton);
        loadingView = view.findViewById(R.id.loadingView);

        // Setup RecyclerView
        adapter = new ExamPapersAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Setup observers
        setupObservers();

        // Setup filter button
        applyFilterButton.setOnClickListener(v -> applyFilters(subjectCodeEdit, yearEdit, semesterEdit));

        // Load initial data
        viewModel.loadExamPapers(null, null, null);
    }

    private void setupObservers() {
        viewModel.getExamPapers().observe(getViewLifecycleOwner(), papers -> {
            adapter.setExamPapers(papers);
            if (papers.isEmpty()) {
                showMessage("No exam papers found");
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (loadingView != null) {
                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showMessage(error);
            }
        });
    }

    private void applyFilters(TextInputEditText subjectCodeEdit,
                              TextInputEditText yearEdit,
                              TextInputEditText semesterEdit) {
        String subjectCode = subjectCodeEdit.getText() != null ?
                subjectCodeEdit.getText().toString().trim() : null;

        Integer year = null;
        if (yearEdit.getText() != null && !yearEdit.getText().toString().isEmpty()) {
            try {
                year = Integer.parseInt(yearEdit.getText().toString());
                if (year < 1900 || year > 2100) {
                    showMessage("Please enter a valid year");
                    return;
                }
            } catch (NumberFormatException e) {
                showMessage("Invalid year format");
                return;
            }
        }

        Integer semester = null;
        if (semesterEdit.getText() != null && !semesterEdit.getText().toString().isEmpty()) {
            try {
                semester = Integer.parseInt(semesterEdit.getText().toString());
                if (semester < 1 || semester > 3) {
                    showMessage("Semester must be between 1 and 3");
                    return;
                }
            } catch (NumberFormatException e) {
                showMessage("Invalid semester format");
                return;
            }
        }

        viewModel.loadExamPapers(subjectCode, year, semester);
    }


    @Override
    public void onViewClick(ExamPaper paper) {
        if (getContext() == null) return;

        // Check if file URL exists
        if (paper.getFileUrl() == null || paper.getFileUrl().isEmpty()) {
            showMessage("PDF URL not available");
            return;
        }

        // Create title for the viewer
        String title = String.format("%s (%s)",
                paper.getSubjectName(),
                paper.getSubjectCode());

        // Launch PDF viewer
        Intent intent = PdfViewerActivity.createIntent(
                requireContext(),
                paper.getFileUrl(),
                title
        );
        startActivity(intent);
    }


    @Override
    public void onDownloadClick(ExamPaper paper) {
        if (getContext() == null) return;

        String directUrl = DriveUrlConverter.convertToDirect(paper.getFileUrl());

        DownloadManager downloadManager = (DownloadManager) requireContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(directUrl))
                .setTitle(paper.getSubjectName() + " Exam Paper")
                .setDescription("Downloading exam paper for " + paper.getSubjectCode())
                .setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "ExamPapers/" + paper.getSubjectCode() + "_" +
                                paper.getYear() + "_" + paper.getSemester() + ".pdf");

        downloadManager.enqueue(request);
        showMessage("Download started");
    }

    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loadingView = null;
    }
}