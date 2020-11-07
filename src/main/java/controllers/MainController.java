package controllers;

import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

public class MainController
{
	
    @FXML
    private GridPane gp;
    private DataFormat paneFormat;
    private DataFormat accFormat;
    
    /**
     * @param event - MouseEvent of the detected drag
     * 
     * This is activated when a drag is detected on a TitledPane
     * (the encasing ones, TitledPane1, TitledPane2, TitledPane3)
     */
    @FXML
    public void TitledPane_OnDragDetected(MouseEvent event)
    {
    	// Begin the dragging event
    	TitledPane dragPane = (TitledPane) event.getSource();
    	Dragboard db = dragPane.startDragAndDrop(TransferMode.ANY);
		db.setDragView(dragPane.snapshot(null, null)); 
		
		// Add the pane format to the clipboard
		// We're using the clipboard for the actual dragging
		ClipboardContent clip = new ClipboardContent();
		Long randNum = (new Random()).nextLong();
		String paneFormatName = "TitledPaneDragging" + String.valueOf(randNum);
		paneFormat = new DataFormat(paneFormatName);
		clip.put(paneFormat, " ");
		
		// Set the content of the dragboard to the clip that we created
		db.setContent(clip);
    }
    
    /**
     * @param event - DragEvent of the drag on drop
     * @return the TitledPane that it was dropped inside of. If none found, returns null
     * 
     * This handles finding the TitledPane that the dragging pane was dropped
     * inside of. This is more complicated than you would think because there
     * are automatically generated labels and other nodes inside of the TitledPane
     * that we have to consider.
     */
	private TitledPane getDroppedTitledPane(DragEvent event)
    {
		// Get the event target
    	Node target = (Node) event.getTarget();
    	
    	// If our target ever gets to the GridPane or null, then we know
    	// that we haven't dropped inside of a TitledPane within the
    	// GridPane
    	//
    	// This could also probably be done with recursion, but this is
    	// how I sporadically wrote this method, so this is what I'm going with
    	while (! target.equals(gp))
    	{
    		// If the parent of the element is the GridPane, we know
    		// that we have a TitledPane that we can cast from Node,
    		// and also we have the TitledPane that we dropped inside of
    		// so let's return it
    		if (target.getParent().equals(gp))
    			return (TitledPane) target;
    		
    		// Get the next level up parent
    		target = target.getParent();
    	}
    	
    	// At this point we know that we don't have the TitledPane, so we just
    	// return null
    	return null;
    }
	
	/**
	 * @param event - DragEvent to complete
	 */
	private void completeDrag(DragEvent event)
	{
		// Set the drag event complete and nullify the paneFormat
		event.setDropCompleted(true);
		paneFormat = null;
	}

	/**
	 * @param event - DragEvent that we are working with
	 */
    @FXML
    public void GridPane_OnDragDropped(DragEvent event)
    {
    	// Get the dragboard and the replacement and dragging panes
        Dragboard db = event.getDragboard();
        TitledPane replacement = getDroppedTitledPane(event);
        TitledPane dragPane = (TitledPane) event.getGestureSource();
        
        // Make sure the dragboard has the pane and make sure the replacement
        // and dragPane are not null. If they're null, the drag & drop was invalid
        // so we just end it and do nothing
        if (db.hasContent(paneFormat) && replacement != null && dragPane != null)
        {
        	// Get the parent of the replacement (this is where we're going)
        	GridPane parent = (GridPane) replacement.getParent();

        	// Set the replacement and dragPane column indices to 0
        	int colReplacement = 0;
        	int colDragPane = 0;
        	
        	// For some reason, only if the index of the Pane is 0 in the GridPane,
        	// it throws a NullPointerException, so if this happens, leave the index
        	// as 0. If it works though, we get the new index
        	try { colReplacement = GridPane.getColumnIndex(replacement); }
        	catch (NullPointerException e) {}
        	
        	try { colDragPane = GridPane.getColumnIndex(dragPane); }
        	catch (NullPointerException e) {}
        	
        	// If the column of the replacement and dragPane are equal, the dragPane
        	// was dropped on top of itself, so we do nothing
        	if (colReplacement == colDragPane)
        	{
        		completeDrag(event);
        		return;
        	}
            
        	// Now, we swap by removing both of the panes and then inserting them
        	// into the appropriate spots
            parent.getChildren().removeAll(dragPane, replacement);
            parent.add(dragPane, colReplacement, 0);
            parent.add(replacement, colDragPane, 0);
            
            // Complete the drag
            completeDrag(event);
        }
        else
        {
        	// Complete the drag
        	completeDrag(event);
        }
    }

    /**
     * @param event - DragEvent that is going on
     * 
     * I'm not 100% sure why this is necessary, but if we don't have it,
     * the dragging all breaks.
     */
    @FXML
    public void GridPane_OnDragOver(DragEvent event)
    {
    	// Get the dragboard
        Dragboard db = event.getDragboard();
        
        // Confirm that the drag is still going on
        if (paneFormat != null && db.hasContent(paneFormat))
            event.acceptTransferModes(TransferMode.ANY);
    }
    
    /*************************************************************************/
    /* END GridPane Dragging    											 */
    /* BEGIN Accordion Dragging 											 */
    /*************************************************************************/
    
    /**
     * @param event - MouseEvent of the dragging
     * 
     * This works exactly the same as above, just for the accordion.
     */
    @FXML
    public void Accordion_OnDragDetected(MouseEvent event)
    {
    	// Begin the dragging event
    	TitledPane dragPane = (TitledPane) event.getSource();
    	Dragboard db = dragPane.startDragAndDrop(TransferMode.ANY);
		db.setDragView(dragPane.snapshot(null, null)); 
		
		// Add the pane format to the clipboard
		ClipboardContent clip = new ClipboardContent();
		Long randNum = (new Random()).nextLong();
		String paneFormatName = "AccordionDragging" + String.valueOf(randNum);
		accFormat = new DataFormat(paneFormatName);
		clip.put(accFormat, " ");
		db.setContent(clip);
    }
    
    /**
     * @param event - DragEvent of the dragging that is occuring
     * @return the TitledPane that was dropped inside of. Returns null if none found
     * 
     * This again works exactly the same as above, except
     * this time we are looking for children of an Accordion
     */
	private TitledPane getDroppedAccordion(DragEvent event)
    {
    	Node target = (Node) event.getTarget();
    	
    	while (! target.equals(gp))
    	{
    		if (target.getParent().getClass().equals(Accordion.class))
    		{
    			return (TitledPane) target;
    		}
    		
    		target = target.getParent();
    	}
    	
    	return null;
    }
	
	/**
	 * @param event - DragEvent to complete
	 */
	private void completeDragAccordion(DragEvent event)
	{
		event.setDropCompleted(true);
		accFormat = null;
	}

	/**
	 * @param event - DragEvent that is dropped
	 */
    @FXML
    public void Accordion_OnDragDropped(DragEvent event)
    {
    	// Get the dragboard, the dragPane, and the replacement
        Dragboard db = event.getDragboard();
        TitledPane replacement = getDroppedAccordion(event);
        TitledPane dragPane = (TitledPane) event.getGestureSource();
        
        // Make sure the replacement and dragPane are not null and the
        // drag was actually occuring
        if (db.hasContent(accFormat) && replacement != null && dragPane != null)
        {
        	// Get the parent of both the dragPane and the replacement.
        	// Remember since we allow switching between different cards,
        	// they may not have the same parent
        	Accordion parentReplacement = (Accordion) replacement.getParent();
        	Accordion parentDragPane = (Accordion) dragPane.getParent();

        	// Switching is going to be interesting here...
        	// 
        	int dragPaneIndex = parentDragPane.getPanes().indexOf(dragPane);
        	int replacementIndex = parentReplacement.getPanes().indexOf(replacement);
        	
        	// If the parents are the same and the indices are the same of the
        	// dragPane and the replacement, finish the drag because we dropped
        	// the dragPane on top of itself
        	if (parentReplacement.equals(parentDragPane) && dragPaneIndex == replacementIndex)
        	{
        		completeDragAccordion(event);
        		return;
        	}
        	
        	// Accordion doesn't allow duplicate entries, so we need temp values
        	// in order to swap the panes. Let's create these temp TitledPanes,
        	// but we don't need anythig inside of them since they're going to
        	// exist for a split second...split second of fame...
        	TitledPane temp1 = new TitledPane();
        	TitledPane temp2 = new TitledPane();
        	
        	// Now, we get slightly complicated. Set the temps as placeholders for
        	// the panes in the accordions. If we don't do it this way, we may lose
        	// the spot in the index and if the index doesn't exist, we cannot use set
        	parentDragPane.getPanes().set(dragPaneIndex, temp1);
        	parentReplacement.getPanes().set(replacementIndex, temp2);
        	
        	// Now, we are going to swap the panes. So, we put the replacement in the
        	// parent accordions and we place the replacement into the dragPane parent
        	// at the dragPaneIndex, and the dragPane in the replacement parent at the
        	// replacementIndex
        	parentDragPane.getPanes().set(dragPaneIndex, replacement);
        	parentReplacement.getPanes().set(replacementIndex, dragPane);
        	
        	// I just want to set temp1 and temp2 to null and suggest the garbage collector
        	// to do some collecting in case there is a lot of swapping going on.
        	// This is probably unnecessary and may not do anything, but memory is
        	// a precious resource especially if this gets really big
        	temp1 = null;
        	temp2 = null;
        	System.gc();
            
        	// Complete the drag
            completeDragAccordion(event);
        }
        else
        {
        	// Complete the drag
        	completeDragAccordion(event);
        }
    }

    /**
     * @param event - DragEvent that is occuring
     * 
     * Still not 100% sure why this is necessary, but again it breaks without it.
     * This again works exactly the same as above
     */
    @FXML
    public void Accordion_OnDragOver(DragEvent event)
    {
        Dragboard db = event.getDragboard();
        
        if (accFormat != null && db.hasContent(accFormat))
            event.acceptTransferModes(TransferMode.ANY);
    }

}
