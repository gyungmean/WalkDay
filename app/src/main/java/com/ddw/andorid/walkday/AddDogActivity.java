package com.ddw.andorid.walkday;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddDogActivity extends Activity {

    final static String TAG = "AddDogActivity";
    final static int PICK_FROM_ALBUM = 1;

    DogDTO newDog;

    EditText etAddName;
    EditText etAddBirthY;
    EditText etAddBirthM;
    EditText etAddBirthD;
    EditText etAddWeight;
    EditText etAddType;
    CheckBox cbWo;
    CheckBox chMa;
    CheckBox chNone;

    ImageView imDog;
    private Uri imagePath;

    DogDBHelper helper;
    SQLiteDatabase dogDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);
        Log.d(TAG, "AddDogActivity START!");
        helper = new DogDBHelper(getApplicationContext());
        dogDB = helper.getWritableDatabase();

        newDog = new DogDTO();

        etAddName = (EditText) this.findViewById(R.id.etAddName);
        etAddBirthY = (EditText) this.findViewById(R.id.etAddBirthY);;
        etAddBirthM = (EditText) this.findViewById(R.id.etAddBirthM);;
        etAddBirthD = (EditText) this.findViewById(R.id.etAddBirthD);;
        etAddWeight = (EditText) this.findViewById(R.id.etAddWeight);;
        etAddType = (EditText) this.findViewById(R.id.etAddType);;
        cbWo = (CheckBox) this.findViewById(R.id.cbWo);
        chMa = (CheckBox) this.findViewById(R.id.chMa);
        chNone = (CheckBox) this.findViewById(R.id.chNone);

        imDog = (ImageView) this.findViewById(R.id.imDog);

    }

    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnAddFinish:
                Log.d(TAG, "btnAddFinish touched");

                //dog db에 저장
                try{
                    newDog.setName(etAddName.getText().toString());
                    newDog.setBirthY(etAddBirthY.getText().toString());
                    newDog.setBirthM(etAddBirthM.getText().toString());
                    newDog.setBirthD(etAddBirthD.getText().toString());
                    newDog.setWeight(Float.parseFloat(etAddWeight.getText().toString()));
                    newDog.setType(etAddType.getText().toString());

                    int[] temp = new int[3];
                    if(cbWo.isChecked()) {
                        temp[0] = 1;
                    }
                    if(chMa.isChecked()) {
                        temp[1] = 1;
                    }
                    if(chNone.isChecked()){
                        temp[2] = 1;
                    }
                    newDog.setGender(temp);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "입력되지 않은 부분이 있습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }

                ContentValues row = new ContentValues();

                row.put(DogDBHelper.COL_NAME, newDog.getName());
                row.put(DogDBHelper.COL_BIRTHY, newDog.getBirthY());
                row.put(DogDBHelper.COL_BIRTHM, newDog.getBirthM());
                row.put(DogDBHelper.COL_BIRTHD, newDog.getBirthD());
                row.put(DogDBHelper.COL_WEIGHT, newDog.getWeight());
                row.put(DogDBHelper.COL_TYPE, newDog.getType());
                row.put(DogDBHelper.COL_GENDER, newDog.getGender());
                row.put(DogDBHelper.COL_PATH, newDog.getPath());

                dogDB.insert(DogDBHelper.TABLE_NAME, null, row);

                Toast.makeText(getApplicationContext(), "추가완료", Toast.LENGTH_SHORT).show();
                helper.close();
                finish();
                break;
            case R.id.btnSelectImage:
                //사진 불러오기
                Log.d(TAG, "btnSelectImage touched");
                Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, PICK_FROM_ALBUM);
                break;

        }

        if (intent != null) startActivity(intent);
    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK) return;

        switch(requestCode) {
            case PICK_FROM_ALBUM:
                imagePath = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    imDog.setImageURI(imagePath);    // 선택한 이미지 이미지뷰에 셋
//                    saveBitmapToJpeg(imgBitmap);    // 내부 저장소에 저장
                    Toast.makeText(getApplicationContext(), "사진 불러오기 성공", Toast.LENGTH_SHORT).show();
                    newDog.setPath(imagePath.toString());
                    Log.d(TAG, "newDog setPath : " + imagePath);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "사진 불러오기 실패", Toast.LENGTH_SHORT).show();
                }

        }

    }

}
