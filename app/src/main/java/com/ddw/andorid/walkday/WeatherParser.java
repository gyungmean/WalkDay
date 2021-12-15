package com.ddw.andorid.walkday;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class WeatherParser {

    final static String TAG = "WeatherParser";

    private enum TagType {NONE, CATEGORY, FCSTVALUE, FCSTTIME, RESULTCODE};
    private enum CategoryType {NONE, POP, SKY, PTY, TMP, TMN, TMX}

//    private final static String ITEM_TAG = "item";
    private final static String CATEGORY_TAG = "category"; //기상정보 종류코드
    private final static String FCSTVALUE_TAG = "fcstValue"; //값
    private final static String RESULTCODE_TAG= "resultCode"; //오류발생시
    private final static String FCSTTIME_TAG= "fcstTime"; //기상예보시각
    private final static String ITEMS_TAG = "items"; //전체 태그 끝부분

    private boolean timeFlag = false;

    private final static String C_POP = "POP"; //강수확률
    private final static String C_SKY = "SKY"; //하늘상태
    private final static String C_PTY = "PTY"; //강수형태
    private final static String C_TMP = "TMP"; //현재기온
    private final static String C_TMN = "TMN"; //최저기온
    private final static String C_TMX = "TMX"; //최고기온

    private XmlPullParser parser;

    public WeatherParser(){
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public WeatherDTO parse(String xml, String now){ //now 현재시간
        WeatherDTO result = new WeatherDTO();
        WeatherDTO dto;
        TagType tagType = TagType.NONE;
        CategoryType categoryType = CategoryType.NONE;

        Log.d(TAG, "now: " + now);

        try {
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();

            dto = new WeatherDTO();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName(); //태그 자체의 이름을 알아내는 코드 <태그> 에서 태그 부분 get
                        if (tag.equals(ITEMS_TAG)) { //새로운 항목을 표현하는 태그를 만났을 경우 DTO객체 생성
                            result = new WeatherDTO();
                        } else if (tag.equals(CATEGORY_TAG)) {
                            tagType = TagType.CATEGORY;
                        } else if (tag.equals(FCSTVALUE_TAG)) {
                            tagType = TagType.FCSTVALUE;
                        } else if (tag.equals(FCSTTIME_TAG)) {
                            tagType = TagType.FCSTTIME;
                        }else if (tag.equals(RESULTCODE_TAG)) {
                            tagType = TagType.RESULTCODE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEMS_TAG)) {
                            Log.d(TAG, "parsing end");
                            result = dto;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case FCSTTIME: //예보시각과 비교
                                if(parser.getText().equals(now)){ //현재 타임 정보만 가지고 오기 위해서
                                    timeFlag = true;
                                } else{
                                    timeFlag = false;
                                }
                                break;
                            case CATEGORY:
                                //NONE, POP, SKY, PTY, TMP, TMN, TMX 정보만 필요로함
                                String category = parser.getText();
                                if(category.equals(C_POP)){
                                    categoryType = CategoryType.POP;
                                } else if(category.equals(C_SKY)){
                                    categoryType = CategoryType.SKY;
                                } else if(category.equals(C_PTY)){
                                    categoryType = CategoryType.PTY;
                                } else if(category.equals(C_TMP)){
                                    categoryType = CategoryType.TMP;
                                } else if(category.equals(C_TMN)){
                                    categoryType = CategoryType.TMN;
                                } else if(category.equals(C_TMX)){
                                    categoryType = CategoryType.TMX;
                                }
                                break;
                            case FCSTVALUE: //기상정보값
                                if(!timeFlag) break; //timeFlag가 false이면 현재시각 정보가 아님
                                String value = parser.getText();
                                switch(categoryType){
                                    case POP:
                                        dto.setPop(Integer.parseInt(value));
                                        break;
                                    case SKY:
                                        dto.setSky(Integer.parseInt(value));
                                        break;
                                    case PTY:
                                        dto.setPty(Integer.parseInt(value));
                                        break;
                                    case TMP:
                                        dto.setTmp(Integer.parseInt(value));
                                        break;
                                    case TMN:
                                        dto.setMin(Integer.parseInt(value));
                                        break;
                                    case TMX:
                                        dto.setMax(Integer.parseInt(value));
                                        break;
                                }
                                categoryType = CategoryType.NONE;
                                break;
                            case RESULTCODE:
                                String code = parser.getText();
                                if(!code.equals("00")){ //00일때 정상결과
                                    Log.e(TAG, "error code: " + code);
                                    return null;
                                }
                                Log.d(TAG, "success");
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}
