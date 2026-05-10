package xin.chunming;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    static ArrayList<String> arr = new ArrayList<>();
    static String elementValue = null;
//    //public static void aaa(String mp3Url, String elementValue) {
//        System.out.println("=== MP3 detected ===");
//        System.out.println("URL:     " + mp3Url);
//        System.out.println("Element: " + elementValue);
//        // 在这里添加你的业务逻辑
//    }

    public static void main(String[] args) {
  for (int i = 1 ;i < 130; i++) {
            int a = 340 + i;
             page("https://weixin60536.video-rays.com/audio/W50536/C1000139/A1545" + a + "/audio/home?adviserId=1000010836&sceneId=3288328&isPreview=1&source_type=QRCODE&appType=AUDIO&userCode=2222222222f&date=2026-05-05&topsize=1&bookId=4908958&timestamp=1778386602458\n");

        }

    }

    private static void page(String url1) {
        System.out.println(url1);
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setArgs(Arrays.asList(
                    "--disable-blink-features=AutomationControlled",  // 移除自动化标识
                    "--disable-dev-shm-usage",
                    "--no-sandbox",
                    "--disable-web-security",
                    "--disable-features=IsolateOrigins,site-per-process"
            )));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // 监听所有请求
            page.onRequest(request -> {
                String url = request.url();
                // System.out.println(url);
                if (url.contains(".mp3") || url.matches(".*audio.*\\.mp3.*") ||
                        request.resourceType().equals("media") && url.contains("mp3")) {
                    System.out.println("Catch:  " + url);
                    try {
                        // 获取页面上 id="a" 的元素的值
//                        String elementValue = (String) page.evaluate(
//                                "() => { " +
//                                        "  const el = document.getElementsByClassName('title')[0]; " +
//                                        "  if (!el) return null; " +
//                                        "  return el.value !== undefined ? el.value : el.textContent; " +
//                                        "}"
//                        );

                        if (!arr.contains(url)) {
                            downloadVideo(url);
                        }

                    } catch (Exception e) {
                        System.err.println("Failed to get element value: " + e.getMessage());
//                        if (!arr.contains(url)){
//                            downloadVideo(url, new File(elementValue+ "_"+UUID.randomUUID()+".mp3"));
//                        }
                    }
                }
            });

            // 也可以监听响应，更精确地判断 Content-Type
//            page.onResponse(response -> {
//                String url = response.url();
//                try {
//                    String contentType = response.headerValue("content-type");
//                    if (contentType != null && contentType.contains("audio/mpeg")) {
//                        elementValue = (String) page.evaluate(
//                                "() => { " +
//                                        "  const el = document.getElementsByClassName('title')[0]; " +
//                                        "  if (!el) return null; " +
//                                        "  return el.value !== undefined ? el.value : el.textContent; " +
//                                        "}"
//                        );
//                        if (!arr.contains(url)){
//                            downloadVideo(url, new File(elementValue+ "_"+UUID.randomUUID()+".mp3"));
//                        }
//
//                    }
//                } catch (Exception e) {
//                    // 部分响应可能无法读取 header，忽略即可
//                }
//            });

            page.navigate(url1,
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
//new Scanner(System.in).nextLine();
            // 保持页面运行，等待用户操作触发 mp3 请求
            page.waitForTimeout(3000);
            elementValue = (String) page.evaluate(
                    "(function() { " +
                            "  const el = document.getElementsByClassName('title')[0]; " +
                            "  if (!el) return null; " +
                            "  return el.value !== undefined ? el.value : el.textContent; " +
                            "})()"
            );
            Thread.sleep(1500);

            page.evaluate("(function(){const a12 = document.getElementsByClassName('ic-contents-pause')[0];" +
                    "if(a12){a12.click()}})()");


            Thread.sleep(6000);
            //new Scanner(System.in).nextLine();
            browser.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void downloadVideo(String url) {
        File saveFile = new File(elementValue + "_" + UUID.randomUUID() + ".mp3");
        System.out.println(url);
        System.out.println(saveFile.getName());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36")
                //.header("Referer", "https://abooks.hep.com.cn/")
                .header("Sec-Fetch-Site", "same-site")
                .header("sec-fetch-dest", "video")
                .header("sec-ch-ua", "Google Chrome\";v=\"147\", \"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"147")
                .header("sec-ch-ua-platform", "macOS")
                .header("Sec-Fetch-Mode", "no-cors")

                .header("Connection", "keep-alive")
                .build();
/*sec-ch-ua
"Google Chrome";v="147", "Not.A/Brand";v="8", "Chromium";v="147"
sec-ch-ua-mobile
?0
sec-ch-ua-platform
"macOS"
sec-fetch-dest
video
sec-fetch-mode
no-cors
sec-fetch-site
same-site*/
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                // 使用 try-with-resources 自动关闭流
                try (InputStream is = response.body().byteStream();
                     FileOutputStream fos = new FileOutputStream(saveFile)) {

                    byte[] buffer = new byte[8192]; // 8KB 缓冲区
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    System.out.println("下载完成！");
                }
            }
        });
    }
}
