package com.robinlabs.voca;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by oded on 3/27/14.
 */
public class NameMatcher {

    private final Activity activity;
    Map<String, Contact> CONTACTS;


    NameMatcher(Activity activity) {
        this.activity = activity;
        cacheNames();
    }


    private void cacheNames() {

        CONTACTS = new HashMap<String, Contact>();

        PackageManager pm = activity.getPackageManager();

//        long epoch = System.currentTimeMillis() - DateUtils.YEAR_IN_MILLIS;

        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) return;

        Cursor cursor = getPhonesCursor(300);

        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

            String name = (cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));

            if (name == null) continue;

            Integer typeCode = (cursor.getInt(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE)));
            String typeName = "Other " + typeCode;

            if (typeCode == ContactsContract.CommonDataKinds.Phone.TYPE_HOME) {
                typeName = "Home";
            } else if (typeCode == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                typeName = "Mobile";
            } else if (typeCode == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                typeName = "Work";
            } else if (typeCode == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE) {
                typeName = "Work Mobile";
            }

            String title = name;// + " - " + typeName;

            if (phoneNumber == null || name == null || name.length() < 2) continue;


            Uri txtUri = Uri.parse("smsto:" + phoneNumber);
            Intent txtIntent = new Intent(Intent.ACTION_SENDTO, txtUri);
            txtIntent.putExtra("sms_body", "\n\n--Dictated to robingets.me");
            txtIntent.setData(txtUri);


            Contact contact = CONTACTS.get(title);
            if (contact == null) {
                contact = new Contact();
            }
            contact.intent = txtIntent;
            contact.name = title;
            contact.type = typeCode;
            contact.phoneNumber = phoneNumber;
            contact.mostRecentCall = (cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
            contact.callCount++;

            CONTACTS.put(title, contact);

        }
        cursor.close();

    }

    private Cursor getPhonesCursor(int limit) {
        Uri queryUri = android.provider.CallLog.Calls.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.CACHED_NUMBER_TYPE,
                CallLog.Calls.DATE};

        String sortOrder = String.format("%s limit " + limit + " ", CallLog.Calls.DATE + " DESC");

        return activity.getContentResolver().query(queryUri, projection, null, null, sortOrder);
    }


    private void showContactsDialog(final Map<Contact, BigDecimal> contactsWithScores, List<Contact> finalContacts) {

        final String[] buttons = new String[contactsWithScores.size()];

        int i = 0;
        for (Contact contact : finalContacts) {
            buttons[i] = contact.name + " - " + contactsWithScores.get(contact);
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Who?");
        builder.setItems(buttons,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        activity.startActivity(CONTACTS.get(buttons[which]).intent);
                        App.task.currentContact = CONTACTS.get(buttons[which]);
                    }
                }
        );
        builder.create().show();
    }


    private BigDecimal calculateScore(Contact contact, ArrayList<String> matches) {

        TreeSet<BigDecimal> scores = new TreeSet<BigDecimal>();

        BigDecimal score = null;
        for (int i = 0; i < matches.size(); i++) {
            String[] matchParts = matches.get(i).split(" ");
            String[] nameParts = contact.name.split(" ");

            for (String match : matchParts) {
                for (String name : nameParts) {

                    score = new BigDecimal(10000);
                    //the lower the better


                    if (name.equals(match))
                        score.divide(new BigDecimal((i + 1) * 4), 4, RoundingMode.HALF_UP);
                    if (name.contains(match))
                        score.divide(new BigDecimal((i + 1) * 4), 4, RoundingMode.HALF_UP);

//            String subString = matches.get(i).substring(1, matches.get(i).length() - 1);

//            if (contact.name.contains(subString)) score.multiply(new BigDecimal(4));

//            diff_levenshtein()
//            diff_main(text1, text2)

                    diff_match_patch dmp = new diff_match_patch();

                    int levenshtein = dmp.diff_levenshtein(dmp.diff_main(name, match));

                    score = score.multiply(new BigDecimal(i + 1));
                    score = score.multiply(new BigDecimal(levenshtein));
                    scores.add(score);
                }
            }
        }


        return scores.first();
    }

    public void match(String match) {

        ArrayList<String> matches = new ArrayList<String>();
        matches.add(match);

        Toast.makeText(activity, matches.toString(), Toast.LENGTH_SHORT).show();


        final Map<Contact, BigDecimal> contactsWithScores = new HashMap<Contact, BigDecimal>();

        for (Contact contact : CONTACTS.values()) {

            BigDecimal score = calculateScore(contact, matches);

/*            if (score.compareTo(new BigDecimal(1)) == 1) {
//                    startActivity(CONTACTS.get(contactName).intent);
            }*/
            contactsWithScores.put(CONTACTS.get(contact.name), score);

        }

        List<Contact> finalContacts = new ArrayList<Contact>(contactsWithScores.keySet());

        Collections.sort(finalContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact contact2) {
                return contactsWithScores.get(contact).compareTo(contactsWithScores.get(contact2));
            }
        });


        App.task.currentContact = finalContacts.get(0);

//        showContactsDialog(contactsWithScores, finalContacts);
    }
}
