package com.clubmycab;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DistanceTimeEstimateHandler extends DefaultHandler {
	
//	private static final String TAG = "com.clubmycab.clubmycab.DistanceTimeEstimateHandler";
	
	private StringBuilder builder; 
	private boolean isDistance, isDuration;
	public ArrayList<Double> distanceArray, durationArray;
	
  @Override
  public void startDocument() throws SAXException {
      super.startDocument();
      distanceArray = new ArrayList<Double>();
      durationArray = new ArrayList<Double>();
  }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         
        builder = new StringBuilder();
         
        if (localName.equals("DirectionsResponse")) {
            //Log.d(TAG, "startElement DirectionsResponse : " + builder.toString());
        } else if (localName.equals("status")) {
            //Log.d(TAG, "startElement status : " + builder.toString());
        } else if (localName.equals("leg")) {
            //Log.d(TAG, "startElement leg : " + builder.toString());
        } else if (localName.equals("step")) {
            //Log.d(TAG, "startElement step : " + builder.toString());
        } else if (localName.equals("duration")) {
            //Log.d(TAG, "startElement duration : " + builder.toString());
            isDuration = true;
        } else if (localName.equals("distance")) {
            //Log.d(TAG, "startElement distance : " + builder.toString());
            isDistance = true;
        } else if (localName.equals("value")) {
            //Log.d(TAG, "startElement value : " + builder.toString());
        }
    }
     
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
         
        if (localName.equals("DirectionsResponse")) {
            //Log.d(TAG, "endElement DirectionsResponse : " + builder.toString());
        } else if (localName.equals("status")) {
            //Log.d(TAG, "endElement status : " + builder.toString());
        } else if (localName.equals("leg")) {
            //Log.d(TAG, "endElement leg : " + builder.toString());
        } else if (localName.equals("step")) {
            //Log.d(TAG, "endElement step : " + builder.toString());
        } else if (localName.equals("duration")) {
            //Log.d(TAG, "endElement duration : " + builder.toString());
        } else if (localName.equals("distance")) {
            //Log.d(TAG, "endElement distance : " + builder.toString());
        } else if (localName.equals("value")) {
            //Log.d(TAG, "endElement value : " + builder.toString());
            if (isDistance) {
            	isDistance = false;
				distanceArray.add(Double.parseDouble(builder.toString()));
			}
            if (isDuration) {
            	isDuration = false;
				durationArray.add(Double.parseDouble(builder.toString()));
			}
        } 
    }
     
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        builder.append(new String(ch, start, length));
    }

}
