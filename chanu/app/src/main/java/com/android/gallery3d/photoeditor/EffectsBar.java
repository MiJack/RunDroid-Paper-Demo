/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.gallery3d.photoeditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.photoeditor.actions.EffectAction;
import com.android.gallery3d.photoeditor.actions.EffectToolFactory;

/**
 * Effects bar that contains all effects and shows them in categorized views.
 */
public class EffectsBar extends LinearLayout {

    private final LayoutInflater inflater;
    private FilterStack filterStack;
    private EffectsMenu effectsMenu;
    private View effectsGallery;
    private ViewGroup effectToolPanel;
    private EffectAction activeEffect;

    public EffectsBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void initialize(FilterStack filterStack) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar.initialize(com.android.gallery3d.photoeditor.FilterStack)",this,filterStack);try{this.filterStack = filterStack;

        effectsMenu = (EffectsMenu) findViewById(R.id.effects_menu);
        effectsMenu.setOnToggleListener(new EffectsMenu.OnToggleListener() {

            @Override
            public boolean onToggle(boolean isSelected, final int effectsId) {
                com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.EffectsBar$1.onToggle(boolean,int)",this,isSelected,effectsId);try{/*// Create and show effects-gallery only if the clicked toggle isn't selected or it's*/
                /*// selected but showing an active effect instead of effects-gallery. Set the clicked*/
                /*// toggle selected only when its effects-gallery will be created and shown.*/
                boolean select = !isSelected || (effectsGallery == null);
                exit(select ? new Runnable() {

                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar$1$1.run()",this);try{createEffectsGallery(effectsId);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar$1$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar$1$1.run()",this,throwable);throw throwable;}
                    }
                } : null);
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar.initialize(com.android.gallery3d.photoeditor.FilterStack)",this);{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.EffectsBar$1.onToggle(boolean,int)",this);return select;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.EffectsBar$1.onToggle(boolean,int)",this,throwable);throw throwable;}
            }
        });

        setEnabled(false);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar.initialize(com.android.gallery3d.photoeditor.FilterStack)",this,throwable);throw throwable;}
    }

    private void createEffectsGallery(int effectsId) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar.createEffectsGallery(int)",this,effectsId);try{/*// Inflate scrollable effects-gallery and desired effects into effects-bar.*/
        effectsGallery = inflater.inflate(R.layout.photoeditor_effects_gallery, this, false);
        ViewGroup scrollView = (ViewGroup) effectsGallery.findViewById(R.id.scroll_view);
        ViewGroup effects = (ViewGroup) inflater.inflate(effectsId, scrollView, false);
        for (int i = 0; i < effects.getChildCount(); i++) {
            setupEffectListener((EffectAction) effects.getChildAt(i));
        }
        scrollView.addView(effects);
        scrollView.scrollTo(0, 0);
        addView(effectsGallery, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar.createEffectsGallery(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar.createEffectsGallery(int)",this,throwable);throw throwable;}
    }

    private void setupEffectListener(final EffectAction effect) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar.setupEffectListener(com.android.gallery3d.photoeditor.actions.EffectAction)",this,effect);try{effect.setListener(new EffectAction.Listener() {

            @Override
            public void onClick() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar$2.onClick()",this);try{if (isEnabled()) {
                    /*// Set the clicked effect active before exiting effects-gallery.*/
                    activeEffect = effect;
                    exitEffectsGallery();
                    /*// Create effect tool panel first before the factory could create tools within.*/
                    createEffectToolPanel();
                    activeEffect.begin(filterStack,
                            new EffectToolFactory(effectToolPanel, inflater));
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar$2.onClick()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar$2.onClick()",this,throwable);throw throwable;}
            }

            @Override
            public void onDone() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar$2.onDone()",this);try{exit(null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar$2.onDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar$2.onDone()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar.setupEffectListener(com.android.gallery3d.photoeditor.actions.EffectAction)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar.setupEffectListener(com.android.gallery3d.photoeditor.actions.EffectAction)",this,throwable);throw throwable;}
    }

    private void createEffectToolPanel() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar.createEffectToolPanel()",this);try{effectToolPanel = (ViewGroup) inflater.inflate(
                R.layout.photoeditor_effect_tool_panel, this, false);
        ((TextView) effectToolPanel.findViewById(R.id.effect_label)).setText(activeEffect.name());
        addView(effectToolPanel, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar.createEffectToolPanel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar.createEffectToolPanel()",this,throwable);throw throwable;}
    }

    private boolean exitEffectsGallery() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.EffectsBar.exitEffectsGallery()",this);try{if (effectsGallery != null) {
            if (activeEffect != null) {
                /*// Detach the active effect to prevent it stopping effects-gallery from gc.*/
                ViewGroup scrollView = (ViewGroup) effectsGallery.findViewById(R.id.scroll_view);
                ((ViewGroup) scrollView.getChildAt(0)).removeView(activeEffect);
            }
            removeView(effectsGallery);
            effectsGallery = null;
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.EffectsBar.exitEffectsGallery()",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.EffectsBar.exitEffectsGallery()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.EffectsBar.exitEffectsGallery()",this,throwable);throw throwable;}
    }

    private boolean exitActiveEffect(final Runnable runnableOnDone) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.EffectsBar.exitActiveEffect(java.lang.Runnable)",this,runnableOnDone);try{if (activeEffect != null) {
            final SpinnerProgressDialog progressDialog = SpinnerProgressDialog.show(
                    (ViewGroup) getRootView().findViewById(R.id.toolbar));
            activeEffect.end(new Runnable() {

                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsBar$3.run()",this);try{progressDialog.dismiss();
                    View fullscreenTool = getRootView().findViewById(R.id.fullscreen_effect_tool);
                    if (fullscreenTool != null) {
                        ((ViewGroup) fullscreenTool.getParent()).removeView(fullscreenTool);
                    }
                    removeView(effectToolPanel);
                    effectToolPanel = null;
                    activeEffect = null;
                    if (runnableOnDone != null) {
                        runnableOnDone.run();
                    }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsBar$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsBar$3.run()",this,throwable);throw throwable;}
                }
            });
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.EffectsBar.exitActiveEffect(java.lang.Runnable)",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.EffectsBar.exitActiveEffect(java.lang.Runnable)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.EffectsBar.exitActiveEffect(java.lang.Runnable)",this,throwable);throw throwable;}
    }

    /**
     * Exits from effects gallery or the active effect; then executes the runnable if applicable.
     *
     * @return true if exiting from effects gallery or the active effect; otherwise, false.
     */
    public boolean exit(final Runnable runnableOnDone) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.EffectsBar.exit(java.lang.Runnable)",this,runnableOnDone);try{/*// Exit effects-menu selected states.*/
        effectsMenu.clearSelected();

        if (exitActiveEffect(runnableOnDone)) {
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.EffectsBar.exit(java.lang.Runnable)",this);return true;}
        }

        boolean exited = exitEffectsGallery();
        if (runnableOnDone != null) {
            runnableOnDone.run();
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.EffectsBar.exit(java.lang.Runnable)",this);return exited;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.EffectsBar.exit(java.lang.Runnable)",this,throwable);throw throwable;}
    }
}
