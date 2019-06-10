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

// 구글에서 제공하는 오픈 소스 라이브러리인 zxing을 사용해 QR코드 스캐너를 구현.
// QR 코드를 인식하게 되면 리뷰를 작성할 수 있는 페이지로 전환되게 하는 액티비티이다.

public class QrScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private BeepManager beepManager;
    private String lastText;
    ArrayList<PlaceDTO> items;
    String url,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        Intent intent=getIntent();
        id= intent.getStringExtra("id");

        //뷰를 바인딩한다.
        ButterKnife.bind(this);

        initLayout();
    }

    @BindView(R.id.zxing_barcode_scanner)
    DecoratedBarcodeView barcodeScannerView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for(PlaceDTO dto:items) {
                if (lastText.equals(dto.getQrcode())) { // 데이터베이스에 등록된 QR코드 값과 현재 인식했던 QR코드의 값이 일치한다면 해당 상점의 리뷰 페이지로 전환되는 코드이다.
                    url=lastText;
                    Intent intent = new Intent(QrScanActivity.this, ReviewWriteActivity.class);

                    intent.putExtra("url",url); // reviewWrite 로 url 값을 전달해준다.
                    intent.putExtra("id",id);  // reviewWrite 로 id 값을 전달해준다.
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    public void onBackPressed() { //뒤로가기 버튼을 클릭하면 초기화면으로 전환된다.
        Intent intent = new Intent(QrScanActivity.this,FirstActivity.class);
        startActivity(intent);
        finish();
    }

    private void initLayout() { // zxing 라이브러리를 사용할 때 필요한 메소드이다.

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

            //Line 100 ~ 104. 한번 인식했던 QR코드를 중복해서 인식할 수 없게 해준다.
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText(); // QR 인식을 통해 얻은 URL 을 lastText 변수에 저장한다.
            Log.i("test", "lastText="+lastText);

            barcodeScannerView.setStatusText(result.getText());

            list(); // 서버와 통신하기 위한 함수를 호출한다.

            //Timber.e("test: " + lastText);
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
                    String page = Common.SERVER_URL + "/place_all_list.php"; // php 파일에 접근하여 가게별 QR 코드를 가져올 수 있도록 한다.
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
                    //핸들러에게 화면 갱신을 요청합니다.
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    // Line 186 ~ 204. zxing 라이브러리를 사용할 때 필요한 메소드 입니다.

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