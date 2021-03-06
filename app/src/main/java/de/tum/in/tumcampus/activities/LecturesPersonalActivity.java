package de.tum.in.tumcampus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.Collections;
import java.util.List;

import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.activities.generic.ActivityForSearchingTumOnline;
import de.tum.in.tumcampus.adapters.LecturesListAdapter;
import de.tum.in.tumcampus.adapters.NoResultsAdapter;
import de.tum.in.tumcampus.auxiliary.LectureSearchSuggestionProvider;
import de.tum.in.tumcampus.models.LecturesSearchRow;
import de.tum.in.tumcampus.models.LecturesSearchRowSet;
import de.tum.in.tumcampus.tumonline.TUMOnlineConst;
import de.tum.in.tumcampus.tumonline.TUMOnlineRequest;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * This activity presents the users' lectures using the TUMOnline web service
 * the results can be filtered by the semester or all shown.
 * 
 * This activity uses the same models as FindLectures.
 * 
 * HINT: a TUMOnline access token is needed
 */
public class LecturesPersonalActivity extends ActivityForSearchingTumOnline<LecturesSearchRowSet> {
    private final static String P_SUCHE = "pSuche";

	/** UI elements */
	private StickyListHeadersListView lvMyLecturesList;

    public LecturesPersonalActivity() {
		super(TUMOnlineConst.LECTURES_PERSONAL, R.layout.activity_lectures, LectureSearchSuggestionProvider.AUTHORITY, 4);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// bind UI elements
		lvMyLecturesList = (StickyListHeadersListView) findViewById(R.id.lvMyLecturesList);

        // handle on click events by showing its LectureDetails
        lvMyLecturesList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                Object o = lvMyLecturesList.getItemAtPosition(position);
                LecturesSearchRow item = (LecturesSearchRow) o;

                // set bundle for LectureDetails and show it
                Bundle bundle = new Bundle();
                // we need the stp_sp_nr
                bundle.putString(LecturesSearchRow.STP_SP_NR, item.getStp_sp_nr());
                Intent intent = new Intent(LecturesPersonalActivity.this, LecturesDetailsActivity.class);
                intent.putExtras(bundle);
                // start LectureDetails for given stp_sp_nr
                startActivity(intent);
            }
        });

        onStartSearch();
	}

    @Override
    protected void onStartSearch() {
        enableRefresh();
        requestHandler = new TUMOnlineRequest<>(TUMOnlineConst.LECTURES_PERSONAL, this, true);
        requestFetch();
    }

    @Override
    protected void onStartSearch(String query) {
        disableRefresh();
        requestHandler = new TUMOnlineRequest<>(TUMOnlineConst.LECTURES_SEARCH, this, true);
        requestHandler.setParameter(P_SUCHE, query);
        requestFetch();
    }

    @Override
    public void onLoadFinished(LecturesSearchRowSet response) {
        if (response == null || response.getLehrveranstaltungen() == null) {
            // no results found
            lvMyLecturesList.setAdapter(new NoResultsAdapter(this));
        } else {
            // Sort lectures by semester id
            List<LecturesSearchRow> lectures = response.getLehrveranstaltungen();
            Collections.sort(lectures);

            // set ListView to data via the LecturesListAdapter
            lvMyLecturesList.setAdapter(new LecturesListAdapter(LecturesPersonalActivity.this, lectures));
        }
    }
}
