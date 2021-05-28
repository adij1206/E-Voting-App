package com.aditya.onlinevoting.Utils;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class SecureQrCode {
    protected Context mContext;
    protected static final byte SEPARATOR_BYTE = (byte)255;
    protected static final int REFERENCE_ID_INDEX = 1,
            NAME_INDEX = 2,
            DATE_OF_BIRTH_INDEX = 3,
            GENDER_INDEX = 4,
            CARE_OF_INDEX = 5,
            DISTRICT_INDEX = 6,
            LANDMARK_INDEX = 7,
            HOUSE_INDEX = 8,
            LOCATION_INDEX = 9,
            PIN_CODE_INDEX = 10,
            POST_OFFICE_INDEX = 11,
            STATE_INDEX = 12,
            STREET_INDEX = 13,
            SUB_DISTRICT_INDEX = 14,
            VTC_INDEX = 15;
    protected int emailMobilePresent, imageStartIndex, imageEndIndex;
    protected ArrayList<String> decodedData;
    //protected AadharCard scannedAadharCard;

    public SecureQrCode(Context activity,String scanData) throws QrCodeException{
        mContext = activity;
        //scannedAadharCard = new AadharCard();

        // 1. Convert Base10 to BigInt
        final BigInteger bigIntScanData = new BigInteger(scanData,10);

        // 2. Convert BigInt to Byte Array
        final byte byteScanData[] = bigIntScanData.toByteArray();

        // 3. Decompress Byte Array
        final byte[] decompByteScanData = decompressData(byteScanData);

        // 4. Split the byte array using delimiter
        List<byte[]> parts = separateData(decompByteScanData);
        // Throw error if there are no parts
        if(parts.size() == 0){
            throw new QrCodeException("Invalid QR Code Data, no parts found after splitting by delimiter");
        }

        // 5. decode extracted data to string
        decodeData(parts);
    }

    /**
     * Decompress the byte array, compression used is GZIP
     * @param byteScanData compressed byte array
     * @return uncompressed byte array
     * @throws QrCodeException
     */
    protected byte[] decompressData(byte[] byteScanData) throws QrCodeException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(byteScanData.length);
        ByteArrayInputStream bin = new ByteArrayInputStream(byteScanData);
        GZIPInputStream gis = null;

        try {
            gis = new GZIPInputStream(bin);
        } catch (IOException e) {
            Log.e("Exception", "Decompressing QRcode, Opening byte stream failed: " + e.toString());
            throw new QrCodeException("Error in opening Gzip byte stream while decompressing QRcode",e);
        }

        int size = 0;
        byte[] buf = new byte[1024];
        while (size >= 0) {
            try {
                size = gis.read(buf,0,buf.length);
                if(size > 0){
                    bos.write(buf,0,size);
                }
            } catch (IOException e) {
                Log.e("Exception", "Decompressing QRcode, writing byte stream failed: " + e.toString());
                throw new QrCodeException("Error in writing byte stream while decompressing QRcode",e);
            }
        }

        try {
            gis.close();
            bin.close();
        } catch (IOException e) {
            Log.e("Exception", "Decompressing QRcode, closing byte stream failed: " + e.toString());
            throw new QrCodeException("Error in closing byte stream while decompressing QRcode",e);
        }

        return bos.toByteArray();
    }

    /**
     * Function to split byte array with delimiter
     * @param source source byte array
     * @return list of separated byte arrays
     */
    protected List<byte[]> separateData(byte[] source) {
        List<byte[]> separatedParts = new LinkedList<>();
        int begin = 0;

        for (int i = 0; i < source.length; i++) {
            if(source[i] == SEPARATOR_BYTE){
                // skip if first or last byte is separator
                if(i != 0 && i != (source.length -1)){
                    separatedParts.add(Arrays.copyOfRange(source, begin, i));
                }
                begin = i + 1;
                // check if we have got all the parts of text data
                if(separatedParts.size() == (VTC_INDEX + 1)){
                    // this is required to extract image data
                    imageStartIndex = begin;
                    break;
                }
            }
        }
        return separatedParts;
    }

    /**
     * function to decode string values
     * @param encodedData
     * @throws QrCodeException
     */
    protected void decodeData(List<byte[]> encodedData) throws QrCodeException{
        Iterator<byte[]> i = encodedData.iterator();
        decodedData = new ArrayList<String>();
        while(i.hasNext()){
            try {
                decodedData.add(new String(i.next(), "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                Log.e("Exception", "Decoding QRcode, ISO-8859-1 not supported: " + e.toString());
                throw new QrCodeException("Decoding QRcode, ISO-8859-1 not supported",e);
            }
        }
        // set the value of email/mobile present flag
        //emailMobilePresent = Integer.parseInt(decodedData.get(0));

        // populate decoded data
        //scannedAadharCard.setName(decodedData.get(2));
        //scannedAadharCard.setDateOfBirth(decodedData.get(3));
    }

    public ArrayList<String> getScannedAadharCard(){
        return decodedData;
    }

}
