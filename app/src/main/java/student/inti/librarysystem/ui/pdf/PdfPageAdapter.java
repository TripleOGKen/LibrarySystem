package student.inti.librarysystem.ui.pdf;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.R;
import android.util.DisplayMetrics;


public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PageViewHolder> {
    private final PdfRenderer pdfRenderer;
    private final int pageCount;

    public PdfPageAdapter(PdfRenderer pdfRenderer) {
        this.pdfRenderer = pdfRenderer;
        this.pageCount = pdfRenderer.getPageCount();
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        try {
            // Open the specific page
            PdfRenderer.Page page = pdfRenderer.openPage(position);

            // Get the display metrics
            DisplayMetrics metrics = holder.itemView.getContext().getResources().getDisplayMetrics();
            float screenWidth = metrics.widthPixels;

            // Calculate desired dimensions while maintaining aspect ratio
            float pageWidth = page.getWidth();
            float pageHeight = page.getHeight();
            float ratio = pageHeight / pageWidth;

            // Use screen width as base and scale height accordingly
            int finalWidth = (int) screenWidth;
            int finalHeight = (int) (screenWidth * ratio);

            // Create a bitmap with the scaled dimensions
            Bitmap bitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);

            // Render the page onto the bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Set the bitmap to the ImageView
            holder.imageView.setImageBitmap(bitmap);

            // Close the page when we're done with it
            page.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return pageCount;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pdfPageImage);
        }
    }

    @Override
    public void onViewRecycled(@NonNull PageViewHolder holder) {
        super.onViewRecycled(holder);
        // Clean up the bitmap to prevent memory leaks
        if (holder.imageView.getDrawable() != null) {
            holder.imageView.setImageBitmap(null);
        }
    }
}