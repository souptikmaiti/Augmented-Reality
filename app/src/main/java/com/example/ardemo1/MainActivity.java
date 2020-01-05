package com.example.ardemo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private static final int CAMERA_REQUEST_CODE= 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arfragment);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            setUpARFragment();

        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                Snackbar.make(findViewById(android.R.id.content), "This App need Camera Permission", Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENABLE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                            }
                        });
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
            }
        }


    }

    private void setUpARFragment(){
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            ModelRenderable.builder()
                    .setSource(this, Uri.parse("ArcticFox_Posed.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                    .exceptionally(throwable -> {
                        new AlertDialog.Builder(this).setMessage(throwable.getMessage()).create().show();
                        return null;
                    });

        });
    }

    private void addModelToScene(Anchor ar, ModelRenderable mr){
        AnchorNode anchorNode = new AnchorNode(ar);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(mr);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE && grantResults != null && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            setUpARFragment();
        }
    }
}
