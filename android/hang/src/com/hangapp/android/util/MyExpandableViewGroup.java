package com.hangapp.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.sdk.ez;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.callback.MyProposalListener;

public class MyExpandableViewGroup extends RelativeLayout implements
        MyProposalListener {

    private static final String TAG = MyExpandableViewGroup.class
            .getSimpleName() + " yello";
    private ResizeAnimation anim;
    private OnClickListener onClick;
    private static boolean isExpanded = false;
    private int originalWidth;
    private int originalHeight;

    private int lastHeight;

    private int shrunkHeight;
    private int expandedCreateHeight;
    private int expandedExistingHeight;
    private int collapsedCreateHeight;
    private boolean isInitialized = false;

    private double createProposalHeightRatio = 0.56;
    private double existingProposalHeightRatio = 0.25;
    private double noProposalHeightRatio = 0.13;

    private Animation animOut = AnimationUtils.loadAnimation(getContext(),
            R.anim.fade_out);
    private Animation animIn = AnimationUtils.loadAnimation(getContext(),
            R.anim.fade_in);

    private final static int DURATION_OF_ANIMATION = 500;

    private Database database;

    public enum Resize {
        CREATE, EXISTING, DEFAULT
    }

    public MyExpandableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.w(TAG, "MYEXPANDABLEVIEWGROUP CONSTRUCTOR CALLED");
        if (!isInEditMode()) {
            database = Database.getInstance();
            initialize(context, database);
        }
    }

    @SuppressWarnings("deprecation")
    public void initialize(Context context, Database database) {

        Log.d(TAG, "expandableView.initialize called");

        if (!isInEditMode()) {

            // Set initial values.
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            // programmatically compute the expanded height
            expandedCreateHeight = (int) (display.getHeight() * createProposalHeightRatio);
            expandedExistingHeight = (int) (display.getHeight() * existingProposalHeightRatio);
            collapsedCreateHeight = (int) (display.getHeight() * noProposalHeightRatio);

            originalWidth = display.getWidth();
            originalHeight = display.getHeight();

            // Handle clicks
            onClick = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toast("clicked");
                    if (!isExpanded) {

                        MyExpandableViewGroup view = (MyExpandableViewGroup) v;
                        // expand(view);
                        resize(view, Resize.CREATE);
                    } else {
                        toast("expanded already");
                    }
                }
            };

            this.setOnClickListener(onClick);

            Log.d(TAG, "Determining whether to resize expandableView");
            // check if needs to be expanded
            if (database != null && database.getMyProposal().isActive()) {
                Log.d(TAG, "expanding to EXISTING height");
                resize(this, Resize.EXISTING);

            } else {
                toast("NOT expanding");
            }

            isInitialized = true;
            Log.d(TAG, "expandableView is initialized.");
        }
    }

    public void setResizeAnimation(ResizeAnimation anim) {
        this.anim = anim;
    }

    public boolean resize(MyExpandableViewGroup view, Resize expansionType) {

        if (!isInitialized) {
            Log.e(TAG, "resize failed. view not initialized.");
            return false;
        }

        // initialize to default height
        int newHeight = collapsedCreateHeight;

        if (expansionType == Resize.EXISTING) {
            newHeight = expandedExistingHeight;
        }
        if (expansionType == Resize.CREATE) {
            newHeight = expandedCreateHeight;
        }

        // resize the view
        view.setResizeAnimation(new ResizeAnimation((View) view, originalWidth,
                view.getHeight(), originalWidth, newHeight,
                DURATION_OF_ANIMATION));

        if (expansionType == Resize.EXISTING || expansionType == Resize.CREATE) {
            anim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    isExpanded = true;
                    Log.d(TAG, "isExpanded set to true");
                }
            });
        } else {
            anim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    isExpanded = false;
                    Log.d(TAG, "isExpanded set to false");
                }
            });
        }

        this.startAnimation(anim);

        Log.d(TAG, "resizing from " + view.getHeight() + " to " + newHeight);

        return true;
    }

    public boolean expand() {
        Log.d(TAG, "expand called");

        if (!isInitialized) {
            Log.e(TAG, "expand failed. expandable_view is not initialized");
            return false;
        }

        // store the last height
        lastHeight = this.getHeight();

        // resize the view
        this.setResizeAnimation(new ResizeAnimation((View) this, originalWidth,
                lastHeight, originalWidth, expandedCreateHeight,
                DURATION_OF_ANIMATION));

        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                isExpanded = true;

            }
        });
        this.startAnimation(anim);

        return true;
    }

    public boolean expandCreate(MyExpandableViewGroup view) {

        if (!isInitialized) {
            Log.e(TAG, "expand failed. expandable_view is not initialized");
            return false;
        }

        lastHeight = view.getHeight();
        Log.d(TAG, "expandCreate() called");

        view.setResizeAnimation(new ResizeAnimation((View) view, originalWidth,
                view.getHeight(), originalWidth, expandedCreateHeight,
                DURATION_OF_ANIMATION));

        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                isExpanded = true;

            }
        });
        this.startAnimation(anim);

        return true;
    }

    public boolean collapse(MyExpandableViewGroup view) {

        if (!isInitialized) {
            Log.e(TAG, "collapse failed. expandable_view is not initialized");
            return false;
        }

        Log.d(TAG, "collapse() called. resizing from height " + view.getHeight() + " to " + collapsedCreateHeight);
        lastHeight = view.getHeight();

        view.setResizeAnimation(new ResizeAnimation((View) view, originalWidth,
                view.getHeight(), originalWidth, collapsedCreateHeight,
                DURATION_OF_ANIMATION));

        this.startAnimation(anim);

        this.isExpanded = false;

        return true;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public void onMyProposalUpdate(Proposal proposal) {
        // TODO Auto-generated method stub
        Toast.makeText(getContext(), "I should expand", Toast.LENGTH_SHORT)
                .show();
    }

    private void toast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

}
