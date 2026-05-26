package com.think_different.think_different.couple.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping("/couple")
public class CoupleQrController {

    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] qr(@RequestParam String code) throws Exception {

        String url = "http://localhost:8080/couple/connect?code=" + code;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(
                url,
                BarcodeFormat.QR_CODE,
                250,
                250
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                outputStream
        );

        return outputStream.toByteArray();
    }
}
