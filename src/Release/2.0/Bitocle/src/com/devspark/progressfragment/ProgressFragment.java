/*
 * Copyright (C) 2013 Evgeny Shishkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devspark.progressfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import io.github.mthli.Bitocle.R;

public class ProgressFragment extends Fragment {
    private View mProgressContainer;
    private View mContentContainer;
    private View mContentView;
    private View mEmptyView;
    private boolean mContentShown;
    private boolean mIsContentEmpty;

    public ProgressFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureContent();
    }

    @Override
    public void onDestroyView() {
        mContentShown = false;
        mIsContentEmpty = false;
        mProgressContainer = mContentContainer = mContentView = mEmptyView = null;
        super.onDestroyView();
    }

    public View getContentView() {
        return mContentView;
    }

    public void setContentView(int layoutResId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View contentView = layoutInflater.inflate(layoutResId, null);
        setContentView(contentView);
    }

    public void setContentView(View view) {
        ensureContent();
        if (view == null) {
            throw new IllegalArgumentException("Content view can't be null");
        }
        if (mContentContainer instanceof ViewGroup) {
            ViewGroup contentContainer = (ViewGroup) mContentContainer;
            if (mContentView == null) {
                contentContainer.addView(view);
            } else {
                int index = contentContainer.indexOfChild(mContentView);
                // replace content view
                contentContainer.removeView(mContentView);
                contentContainer.addView(view, index);
            }
            mContentView = view;
        } else {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
    }

    public void setEmptyText(int resId) {
        setEmptyText(getString(resId));
    }

    public void setEmptyText(CharSequence text) {
        ensureContent();
        if (mEmptyView != null && mEmptyView instanceof TextView) {
            ((TextView) mEmptyView).setText(text);
        } else {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
    }

    public void setContentShown(boolean shown) {
        setContentShown(shown, true);
    }

    public void setContentShownNoAnimation(boolean shown) {
        setContentShown(shown, false);
    }

    private void setContentShown(boolean shown, boolean animate) {
        ensureContent();
        if (mContentShown == shown) {
            return;
        }
        mContentShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.GONE);
        }
    }

    public boolean isContentEmpty() {
        return mIsContentEmpty;
    }

    public void setContentEmpty(boolean isEmpty) {
        ensureContent();
        if (mContentView == null) {
            throw new IllegalStateException("Content view must be initialized before");
        }
        if (isEmpty) {
            mEmptyView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }
        mIsContentEmpty = isEmpty;
    }

    private void ensureContent() {
        if (mContentContainer != null && mProgressContainer != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        mProgressContainer = root.findViewById(R.id.progress_container);
        if (mProgressContainer == null) {
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.progress_container'");
        }
        mContentContainer = root.findViewById(R.id.content_container);
        if (mContentContainer == null) {
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");
        }
        mEmptyView = root.findViewById(android.R.id.empty);
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
        mContentShown = true;

        if (mContentView == null) {
            setContentShown(false, false);
        }
    }
}
