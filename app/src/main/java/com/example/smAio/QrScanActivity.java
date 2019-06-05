package com.example.smAio;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class QrScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private BeepManager beepManager;
    private String lastText;
    ArrayList<PlaceDTO> items;
    String url,id;
    /**
     * view
     **/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        Intent intent=getIntent();
        id= intent.getStringExtra("id");

        //뷰를 바인딩합니다.
        ButterKnife.bind(this);

        initLayout();
    }

    @BindView(R.id.zxing_barcode_scanner)
    DecoratedBarcodeView barcodeScannerView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("test3", "여기까지 넘어온다");
            for(PlaceDTO dto:items) {
                if (lastText.equals(dto.getQrcode())) {
                    url=lastText;
                    Intent intent = new Intent(QrScanActivity.this, ReviewWriteActivity.class);

                    Log.i("test4", id);
                    intent.putExtra("url",url);
                    intent.putExtra("id",id);
                    Log.i("test4", "여기까지 넘어온다");
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시
        Intent intent = new Intent(QrScanActivity.this,FirstActivity.class);
        startActivity(intent);
        finish();
    }

    private void initLayout() {

        barcodeScannerView.setTorchListener(this);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128);
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.initializeFromIntent(getIntent());
        barcodeScannerView.decodeContinuous(callback);
        beepManager = new BeepManager(this);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

//            방금 읽은 qr코드와 같은 qr코드를 읽었을 경우 무시하고 싶으시면 주석을 풀어주세요.
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText();
            Log.i("test", "lastText="+lastText);

            barcodeScannerView.setStatusText(result.getText());

            //여기서 서버와 통신하는 함수를 실행하시면 됩니다.
            list();

            Timber.e("test: " + lastText);
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    void list(){
        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<PlaceDTO>();
                    String page = Common.SERVER_URL + "/place_list.php";
                    Log.i("test", "php연결 완료");

                    URL url = new URL(page);
                    // 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 연결되었으면.
                    if (conn != null) {
                        //타임아웃 시간 설정
                        conn.setConnectTimeout(10000);
                        //캐쉬 사용 여부
                        conn.setUseCaches(false);
                        //url에 접속 성공하면
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            //스트림 생성
                            BufferedReader br =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    conn.getInputStream(), "utf-8"));
                            while (true) {
                                String line = br.readLine(); //한 라인을 읽음
                                if (line == null) break;//더이상 내용이 없으면 종료
                                sb.append(line + "\n");
                            }
                            br.close(); //버퍼 닫기
                        }
                        conn.disconnect();
                    }
// 스트링을 json 객체로 변환
                    JSONObject jsonObj = new JSONObject(sb.toString());

// json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);

                        PlaceDTO dto = new PlaceDTO();

                        dto.setQrcode(row.getString("qrcode"));

                        Log.i("test2",row.getString("place_name"));
                        Log.i("test2",row.getString("qrcode"));

                        if (!row.isNull("image"))
                            dto.setImage(row.getString("image"));

                        items.add(dto);

                    }
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    @Override
    public void onTorchOn() {
    }

    @Override
    public void onTorchOff() {
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}