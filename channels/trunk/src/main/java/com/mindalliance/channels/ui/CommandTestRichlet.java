/**
 * 
 */
package com.mindalliance.channels.ui;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

/**
 * @author dfeeney
 * 
 */
public class CommandTestRichlet extends GenericRichlet {

    private static Button firstButton;
    private static Button secondButton;

    private static Slider firstSlider;

    private static Slider secondSlider;

    private static Radiogroup radioGroup;

    private static Checkbox firstCheck;

    private static Checkbox secondCheck;

    private static Checkbox thirdCheck;
    private static Textbox singleText;
    private static Textbox multiText;
    
    private static Listbox singleList;
    private static Listbox multiList;
    private static Tree tree;
    /*
     * (non-Javadoc)
     * 
     * @see org.zkoss.zk.ui.Richlet#service(org.zkoss.zk.ui.Page)
     */
    public void service(Page page) {
        Window w = new Window("Command Test", "normal", false);

        Hbox box = new Hbox();
        box.appendChild(initButtons(box));
        box.appendChild(initSliders(box));
        box.setParent(w);
        
        box = new Hbox();
        box.appendChild(initRadio());
        box.appendChild(initCheck());
        box.setParent(w);
        
        w.appendChild(initText());
        w.appendChild(initList());
        w.appendChild(initTree());
        w.setPage(page);
    }

    private Groupbox initCheck() {
        Groupbox checkBox = new Groupbox();
        checkBox.appendChild(new Caption("Check boxes"));
        firstCheck = new Checkbox();
        firstCheck.setLabel("First Check");
        firstCheck.addEventListener("onCheck", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("First Check: " + firstCheck.isChecked());
            }
        });
        firstCheck.setParent(checkBox);

        secondCheck = new Checkbox();
        secondCheck.setLabel("Second Check");
        secondCheck.addEventListener("onCheck", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Second Check: " + secondCheck.isChecked());
            }
        });
        secondCheck.setParent(checkBox);

        thirdCheck = new Checkbox();
        thirdCheck.setLabel("Third Check");
        thirdCheck.addEventListener("onCheck", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Third Check: " + thirdCheck.isChecked());
            }
        });
        thirdCheck.setParent(checkBox);
        return checkBox;
    }

    private Groupbox initRadio() {
        // Radio buttons
        Groupbox radiobox = new Groupbox();
        radioGroup = new Radiogroup();
        radioGroup.appendChild(new Radio("Radio 1"));
        radioGroup.appendChild(new Radio("Radio 2"));
        radioGroup.appendChild(new Radio("Radio 3"));

        radioGroup.addEventListener("onCheck", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage(radioGroup.getSelectedItem().getLabel());
            }
        });

        radiobox.appendChild(new Caption("Radio Buttons"));
        radiobox.appendChild(radioGroup);
        return radiobox;
    }

    private Groupbox initSliders(Box box) {
        Groupbox sliderBox = new Groupbox();
        sliderBox.appendChild(new Caption("Sliders"));

        firstSlider = new Slider();
        firstSlider.setWidth("200px");
        firstSlider.addEventListener("onScroll", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Slider 1: " + firstSlider.getCurpos());
            }
        });
        firstSlider.setParent(sliderBox);
        Separator separator = new Separator();
        sliderBox.appendChild(separator);
        secondSlider = new Slider();
        secondSlider.setWidth("200px");
        secondSlider.addEventListener("onScroll", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Slider 2: " + secondSlider.getCurpos());
            }
        });
        secondSlider.setParent(sliderBox);
        return sliderBox;
    }

    private Groupbox initButtons(Box box) {
        Groupbox buttonBox = new Groupbox();
        buttonBox.appendChild(new Caption("Buttons"));
        firstButton = new Button();
        firstButton.addEventListener("onClick", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("First button clicked");
            }
        });
        firstButton.setLabel("First Button");
        firstButton.setParent(buttonBox);
        
        secondButton = new Button();
        secondButton.addEventListener("onClick", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Second button clicked");
            }
        });
        secondButton.setLabel("Second Button");
        secondButton.setParent(buttonBox);
        return buttonBox;
    }
    
    private Groupbox initText() {
        Groupbox textBox = new Groupbox();
        textBox.appendChild(new Caption("Text Fields"));
        singleText = new Textbox();
        singleText.addEventListener("onChange", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Single Text: " + singleText.getValue());
            }
        });
        singleText.setParent(textBox);
        multiText = new Textbox();
        multiText.setRows(5);
        multiText.setCols(40);
        multiText.addEventListener("onChange", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Multi Text: " + multiText.getValue());
            }
        });
        multiText.setParent(textBox);
        return textBox;
    }

    private Groupbox initList() {
        Groupbox listbox = new Groupbox();
        listbox.appendChild(new Caption("List Boxes"));
        singleList = new Listbox();
        singleList.setRows(1);
        singleList.setMold("select");
        singleList.appendItem("Item 1", "Item 1");
        singleList.appendItem("Item 2", "Item 2");
        singleList.appendItem("Item 3", "Item 3");
        singleList.addEventListener("onSelect", new CommandEventListener() {
            public void onEvent(Event event) {
                showMessage("Single List : " + singleList.getSelectedItem().getValue());
            }
        });
        singleList.setParent(listbox);
        
        multiList = new Listbox();
        multiList.setRows(5);
        multiList.setMultiple(true);
        multiList.appendItem("Item 1", "Item 1");
        multiList.appendItem("Item 2", "Item 2");
        multiList.appendItem("Item 3", "Item 3");
        multiList.appendItem("Item 4", "Item 4");
        multiList.appendItem("Item 5", "Item 5");
        multiList.appendItem("Item 6", "Item 6");
        
        multiList.addEventListener("onSelect", new CommandEventListener() {
            public void onEvent(Event event) {
                String result = "MultiList: ";
                for (Object item : multiList.getSelectedItems()) {
                    result += "\n" + ((Listitem)item).getLabel();
                }
                showMessage(result);
            }
        });
        multiList.setParent(listbox);
        return listbox;
    }
    
    private Groupbox initTree() {
        Groupbox treebox = new Groupbox();
        treebox.appendChild(new Caption("Tree"));
        tree = new Tree();
        tree.setRows(5);
        tree.setParent(treebox);
        return treebox;
    }
    
    private static abstract class CommandEventListener implements EventListener {
        public boolean isAsap() {
            return true;
        }

        public abstract void onEvent(Event event);
    }

    private static void showMessage(String message) {

        try {
            Messagebox.show(message, "Warning", Messagebox.OK,
                    Messagebox.EXCLAMATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
