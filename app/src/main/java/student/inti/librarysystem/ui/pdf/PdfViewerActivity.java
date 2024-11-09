package student.inti.librarysystem.ui.pdf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import student.inti.librarysystem.R;
import student.inti.librarysystem.util.DriveUrlConverter;

public class PdfViewerActivity extends AppCompatActivity {
    private static final String EXTRA_PDF_URL = "pdf_url";
    private static final String EXTRA_PDF_TITLE = "pdf_title";

    private ViewPager2 viewPager;
    private TextView pageIndicator;
    private ProgressBar progressBar;
    private String pdfUrl;
    private String pdfTitle;
    private File pdfFile;
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private final OkHttpClient client = new OkHttpClient();


    public static Intent createIntent(Context context, String pdfUrl, String title) {
        Intent intent = new Intent(context, PdfViewerActivity.class);
        intent.putExtra(EXTRA_PDF_URL, pdfUrl);
        intent.putExtra(EXTRA_PDF_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        pageIndicator = findViewById(R.id.pageIndicator);
        progressBar = findViewById(R.id.progressBar);

        // Get intent extras
        pdfUrl = getIntent().getStringExtra(EXTRA_PDF_URL);
        pdfTitle = getIntent().getStringExtra(EXTRA_PDF_TITLE);

        // Set title
        setTitle(pdfTitle != null ? pdfTitle : "PDF Viewer");

        if (pdfUrl != null) {
            loadPdf();
        } else {
            showError("Invalid PDF URL");
        }
    }

    private void loadPdf() {
        showLoading(true);

        try {
            pdfFile = File.createTempFile("temp_pdf", ".pdf", getCacheDir());

            // Convert to direct download URL if it's a Drive link
            String directUrl = DriveUrlConverter.convertToDirect(pdfUrl);

            Request request = new Request.Builder()
                    .url(directUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        showError("Failed to load PDF: " + e.getMessage());
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showError("Failed to load PDF: " + response.code());
                        });
                        return;
                    }

                    try (ResponseBody responseBody = response.body();
                         OutputStream outputStream = new FileOutputStream(pdfFile)) {
                        if (responseBody == null) {
                            runOnUiThread(() -> {
                                showLoading(false);
                                showError("Empty response from server");
                            });
                            return;
                        }

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        InputStream inputStream = responseBody.byteStream();
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        runOnUiThread(() -> displayPdf(pdfFile));
                    }
                }
            });
        } catch (IOException e) {
            showLoading(false);
            showError("Error creating temporary file: " + e.getMessage());
        }
    }

    private void displayPdf(File pdfFile) {
        try {
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);

            PdfPageAdapter adapter = new PdfPageAdapter(pdfRenderer);
            viewPager.setAdapter(adapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updatePageIndicator(position);
                }
            });

            showLoading(false);
        } catch (IOException e) {
            showError("Error opening PDF: " + e.getMessage());
        }
    }

    private void updatePageIndicator(int position) {
        if (pageIndicator != null && pdfRenderer != null) {
            pageIndicator.setText(String.format(Locale.getDefault(),
                    "Page %d of %d", position + 1, pdfRenderer.getPageCount()));
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (viewPager != null) {
            viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showError(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Clean up temporary file
        if (pdfFile != null && pdfFile.exists()) {
            boolean deleted = pdfFile.delete();
            if (!deleted) {
                pdfFile.deleteOnExit();
            }
        }
        client.dispatcher().cancelAll();

    }
}