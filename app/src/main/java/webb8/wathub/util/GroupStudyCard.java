package webb8.wathub.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import webb8.wathub.R;
import webb8.wathub.hub.ProfileActivity;
import webb8.wathub.models.Course;
import webb8.wathub.models.GroupStudy;
import webb8.wathub.models.Parsable;

/**
 * Created by mismayil on 24/02/16.
 */
public class GroupStudyCard extends PostCard {
    private GroupStudy mGroupStudy;
    private TextView mGroupName, mGroupCourse, mGroupWhere, mGroupStartTime, mGroupEndTime, mGroupNumJoined, mGroupMaxPeople;
    private JSONArray mStudentsJoined;          // to avoid too many requests


    public GroupStudyCard(Activity activity, GroupStudy groupStudy) {
        mActivity = activity;
        mGroupStudy = groupStudy;
    }

    private boolean haveJoined (){
        String curObjectid = ParseUser.getCurrentUser().getObjectId();
        if (mStudentsJoined != null) {
            for (int i = 0; i < mStudentsJoined.length(); i++) {
                try {
                    if (mStudentsJoined.getJSONObject(i).get(Parsable.KEY_OBJECT_ID).equals(curObjectid)) {
                        return true;
                    }
                }
                catch (org.json.JSONException e){
                }
            }
        }
        return false;
    }

    private void removeFromStudents (JSONArray Students){
        JSONArray newStudents = new JSONArray();

        String curObjectid = ParseUser.getCurrentUser().getObjectId();
        if (Students != null) {
            for (int i = 0; i < Students.length(); i++) {
                try {
                    if (!Students.getJSONObject(i).get(Parsable.KEY_OBJECT_ID).equals(curObjectid)) {
                        // add to the new list
                        newStudents.put(Students.getJSONObject(i));
                    }
                }
                catch (org.json.JSONException e){
                }
            }
        }

        mGroupStudy.setStudents(newStudents);
        mGroupStudy.saveInBackground();
    }

    private void joinButtonAction (){
        mStudentsJoined = mGroupStudy.getStudents();
        // if the student is in the list, then remove the student
        if (haveJoined()) {
            // remove the student. TODO: add remove after wrapper to parse::remove is defined
            removeFromStudents (mStudentsJoined);

            // update the copy of the Students set
            mStudentsJoined = mGroupStudy.getStudents();

            // update the num of students joined in the textview
            mGroupNumJoined.setText(String.valueOf(mStudentsJoined == null ? 0 : "" + mStudentsJoined.length()));

            // update the text from "Joined" to "Join"
            mPostActionJoinView.setText(mActivity.getString(R.string.action_join));
        }

        // if the student has not beed added to the list AND there is an available seat,then add
        else if (((mStudentsJoined == null) && (mGroupStudy.getMaxPeople() != 0)) ||
                ((mStudentsJoined != null) && (mStudentsJoined.length() < mGroupStudy.getMaxPeople()))) {
            // add the user
            mGroupStudy.addStudent(ParseUser.getCurrentUser());
            mGroupStudy.saveInBackground();

            // update the copy of the Students set
            mStudentsJoined = mGroupStudy.getStudents();

            // update the num of students joined in the textview
            mGroupNumJoined.setText(String.valueOf(mStudentsJoined == null ? 0 : "" + mStudentsJoined.length()));

            // update the text from "Join" to "Joined"
            mPostActionJoinView.setText(mActivity.getString(R.string.action_joined));
        }
        else {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.warning_group_full), Toast.LENGTH_SHORT).show();
        }
    }

    private void mStudentsJoinedUpdate (){
        mStudentsJoined = mGroupStudy.getStudents();
        mGroupNumJoined.setText(String.valueOf(mStudentsJoined == null ? 0 : "" + mStudentsJoined.length()));
        if (haveJoined()){
            mPostActionJoinView.setText(mActivity.getString(R.string.action_joined));
        }
        else {
            mPostActionJoinView.setText(mActivity.getString(R.string.action_join));
        }
    }

    private void joinedStudentsButtonAction (){
        mStudentsJoinedUpdate();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.title_users_joined));
        final List<String> mListOfJoinedStudents = new ArrayList<String>();
        if (mStudentsJoined != null) {
            for (int i = 0; i < mStudentsJoined.length(); i++) {
                try {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    mListOfJoinedStudents.add(query.get(mStudentsJoined.getJSONObject(i).get(Parsable.KEY_OBJECT_ID).toString()).getUsername());
                }
                catch (org.json.JSONException e){} catch (ParseException e) {}
            }
        }
        final CharSequence[] charListOfStudents = mListOfJoinedStudents.toArray(new CharSequence[mListOfJoinedStudents.size()]);
        builder.setItems(charListOfStudents, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mActivity, ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER_NAME, charListOfStudents[which]);
                mActivity.startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(true);
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public View getView(View postCardView) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.card_groupstudy, null, false);
        mGroupName = (TextView) view.findViewById(R.id.group_study_group_name);
        mGroupCourse = (TextView) view.findViewById(R.id.group_study_course);
        mGroupWhere = (TextView) view.findViewById(R.id.group_study_where);
        mGroupStartTime = (TextView) view.findViewById(R.id.group_study_start_time);
        mGroupEndTime = (TextView) view.findViewById(R.id.group_study_end_time);
        mPostActionJoinBarView = (LinearLayout) postCardView.findViewById(R.id.post_action_bar_join);
        mPostActionJoinNumBarView = (LinearLayout) postCardView.findViewById(R.id.post_action_join_num_bar);
        mPostActionJoinView = (TextView) postCardView.findViewById(R.id.post_action_join);
        mGroupNumJoined = (TextView) postCardView.findViewById(R.id.post_action_join_num);
        mGroupMaxPeople = (TextView) postCardView.findViewById(R.id.post_action_join_max_num);

        mPostActionJoinBarView.setVisibility(View.VISIBLE);

        mPostActionJoinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinButtonAction();
            }
        });

        mPostActionJoinNumBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinedStudentsButtonAction();
            }
        });

        Course course = mGroupStudy.getCourse();
        try {
            course.fetch();
        } catch (ParseException e) {

        }

        mGroupName.setText(mGroupStudy.getGroupName());
        mGroupCourse.setText(course.getSubject() + " " + course.getNumber());
        mGroupWhere.setText(mGroupStudy.getWhere());
        String startTime = mGroupStudy.getStartTime().toString();
        startTime = startTime.substring(4, startTime.length()-12);
        mGroupStartTime.setText(startTime);
        String endTime = mGroupStudy.getEndTime().toString();
        endTime = endTime.substring(4, endTime.length() - 12);
        mGroupEndTime.setText(endTime);
        mGroupMaxPeople.setText(String.valueOf(mGroupStudy.getMaxPeople()));

        if (haveJoined()){
            mPostActionJoinView.setText(mActivity.getString(R.string.action_joined));
        }
        else {
            mPostActionJoinView.setText(mActivity.getString(R.string.action_join));
        }

        mStudentsJoinedUpdate();

        return view;
    }

    public void refresh() {

    }
}
