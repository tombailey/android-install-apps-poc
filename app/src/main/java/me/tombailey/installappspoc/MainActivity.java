package me.tombailey.installappspoc;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Environment.*;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_recycler_view_files)
    RecyclerView mFileRecyclerView;

    private FileAdapter mFileAdapter;
    private File mCurrentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    @Override
    public void onBackPressed() {
        File currentFoldersParent = mCurrentFolder.getParentFile();
        if (currentFoldersParent != null && currentFoldersParent.canRead()) {
            listFolderContents(currentFoldersParent);
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        mFileAdapter = new FileAdapter(new FileAdapter.OnFileSelected() {
            @Override
            public void onFileSelected(File file) {
                if (file.isDirectory()) {
                    listFolderContents(file);
                } else if (!file.getAbsolutePath().endsWith(".apk")) {
                    Toast.makeText(MainActivity.this, R.string.invalid_file, Toast.LENGTH_LONG).show();
                } else {
                    install(file);
                }
            }
        });
        mFileRecyclerView.setAdapter(mFileAdapter);
        mFileRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        listFolderContents(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS));
    }

    private void listFolderContents(File folder) {
        mCurrentFolder = folder;
        mFileAdapter.setFiles(folder.listFiles());
    }

    private void install(File apk) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        startActivity(install);
    }
}
