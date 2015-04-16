package com.newsdemo;

/**
 * Created by fida on 3/24/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllNewsFragment extends ListFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    // All static variables
    private String URL = "";
    // XML node keys
    static final String KEY_ITEM = "item"; // parent node
    static final String KEY_LINK = "link";
    static final String KEY_NAME = "title";
    static final String KEY_DATE = "pubDate";
    static final String KEY_DESC = "description";
    private ArrayList<HashMap<String, String>> newsItems = new ArrayList<HashMap<String, String>>();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AllNewsFragment newInstance(int sectionNumber) {
        AllNewsFragment fragment = new AllNewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AllNewsFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // selecting single ListView item
        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String url = newsItems.get(position).get(KEY_LINK);

                // Starting new intent
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });


        setUrl(getArguments().getInt(ARG_SECTION_NUMBER));
        processXml();


    }


    public void setUrl(int p) {
        switch (p) {
            case 1:
                URL = "http://feeds.bbci.co.uk/news/rss.xml";
                break;
            case 2:
                URL = "http://feeds.bbci.co.uk/news/world/rss.xml";
                break;
            case 3:
                URL = "http://feeds.bbci.co.uk/news/uk/rss.xml";
                break;
            case 4:
                URL = "http://feeds.bbci.co.uk/news/business/rss.xml";
                break;
            case 5:
                URL = "http://feeds.bbci.co.uk/news/politics/rss.xml";
                break;
            case 6:
                URL = "http://feeds.bbci.co.uk/news/health/rss.xml";
                break;
            case 7:
                URL = "http://feeds.bbci.co.uk/news/education/rss.xml";
                break;
            case 8:
                URL = "http://feeds.bbci.co.uk/news/science_and_environment/rss.xml";
                break;
            case 9:
                URL = "http://feeds.bbci.co.uk/news/technology/rss.xml";
                break;
            case 10:
                URL = "http://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml";
                break;

        }
    }

    public void processXml() {

        final ProgressDialog progressDialog2 = new ProgressDialog(
                getActivity());
        progressDialog2.setTitle("Loading...");
        progressDialog2.setMessage("Fetching News...");
        // method stub
        progressDialog2.show();
        progressDialog2.setIndeterminate(true);
        progressDialog2.setCancelable(false);

        final Handler mHandler = new Handler();
        // Function to run after thread
        final Runnable mUpdateResults = new Runnable() {
            public void run() {
                progressDialog2.dismiss();
                if (newsItems.size() > 0) {
                    // Adding menuItems to ListView
                    ListAdapter adapter = new SimpleAdapter(getActivity(), newsItems,
                            R.layout.list_item,
                            new String[]{KEY_NAME, KEY_DESC, KEY_DATE}, new int[]{
                            R.id.name, R.id.desciption, R.id.cost});

                    setListAdapter(adapter);
                }

            }
        };

        new Thread() {
            public void run() {

                try {


                    XMLParser parser = new XMLParser();
                    String xml = parser.getXmlFromUrl(URL); // getting XML
                    Document doc = parser.getDomElement(xml); // getting DOM element

                    NodeList nl = doc.getElementsByTagName(KEY_ITEM);
                    // looping through all item nodes <item>
                    for (int i = 0; i < nl.getLength(); i++) {
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        Element e = (Element) nl.item(i);
                        // adding each child node to HashMap key => value
                        map.put(KEY_LINK, parser.getValue(e, KEY_LINK));
                        map.put(KEY_NAME, parser.getValue(e, KEY_NAME));
                        map.put(KEY_DATE, parser.getValue(e, KEY_DATE));
                        map.put(KEY_DESC, parser.getValue(e, KEY_DESC));

                        // adding HashList to ArrayList
                        newsItems.add(map);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.post(mUpdateResults);
            }
        }.start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
