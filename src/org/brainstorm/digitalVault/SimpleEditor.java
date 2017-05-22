package org.brainstorm.digitalVault;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

public class SimpleEditor extends JFrame {

  private Action openAction = new OpenAction();
  private Action saveAction = new SaveAction();

  private JTextComponent textComp;
  private Hashtable actionHash = new Hashtable();
  
  private static final String FILEPATH = "E:\\Program Files\\DigitalVault\\";
  private static final String FILENAME = "myVault.txt";
  private static final String TMPFILE = "myVaultTmp.txt";
  private static final String KEY = "B374A26A71490437";

  // Create an editor.
  public SimpleEditor() {
    super("Swing Editor");
    textComp = createTextComponent();

    Container content = getContentPane();
    content.add(textComp, BorderLayout.CENTER);
    setJMenuBar(createMenuBar());
    setSize(350, 250);
    getOpenAction();
    openAction.actionPerformed(null);
  }

  // Create the JTextComponent subclass.
  protected JTextComponent createTextComponent() {
    JTextArea ta = new JTextArea();
    ta.setLineWrap(true);
    return ta;
  }

  // Create a JMenuBar with file & edit menus.
  protected JMenuBar createMenuBar() {
    JMenuBar menubar = new JMenuBar();
    JMenu file = new JMenu("File");
    menubar.add(file);

    file.add(getSaveAction());
    file.add(new ExitAction());

    return menubar;
  }

  // Subclass can override to use a different open action.
  protected Action getOpenAction() { return openAction; }

  // Subclass can override to use a different save action.
  protected Action getSaveAction() { return saveAction; }

  protected JTextComponent getTextComponent() { return textComp; }

  // ********** ACTION INNER CLASSES ********** //

  // A very simple exit action
  public class ExitAction extends AbstractAction {
    public ExitAction() { super("Exit"); }
    public void actionPerformed(ActionEvent ev) { System.exit(0); }
  }

  // An action that opens an existing file
  class OpenAction extends AbstractAction {
    public OpenAction() { 
      super("Open", new ImageIcon("icons/open.gif")); 
    }

    public void actionPerformed(ActionEvent ev) {
		File dir = new File(FILEPATH);
		if(!dir.exists()){
			dir.mkdirs();
		}
      File file = new File(FILEPATH+FILENAME);
      
      if (file == null)
        return;

      File tmpFile=null;
      FileReader reader = null;
      try {
    	  if(!file.exists())
    		  file.createNewFile();
    	  
		  byte[] outByte = CryptoUtil.decrypt(KEY, file);
		  tmpFile = new File(FILEPATH+TMPFILE);
	      FileOutputStream outputStream = new FileOutputStream(tmpFile);
	   	  outputStream.write(outByte);
	   	  outputStream.close();
	   	  reader = new FileReader(tmpFile);
    
		  textComp.read(reader, null);
		  reader.close();
        
		  //delete temp file
		  if(tmpFile.exists())
		  	tmpFile.delete();
        
      }
      catch (IOException ex) {
        JOptionPane.showMessageDialog(SimpleEditor.this,
        "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
      }catch(Exception e){
    	  e.printStackTrace();
      }
      finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException x) {}
        }
      }
    }
  }

  // An action that saves the document to a file
  class SaveAction extends AbstractAction {
    public SaveAction() {
      super("Save", new ImageIcon("icons/save.gif"));
    }

    public void actionPerformed(ActionEvent ev) {

      FileWriter writer = null;
      try {
    	  
    	File dir = new File(FILEPATH);
  		if(!dir.exists()){
  			dir.mkdirs();
  		}
    	File mainFile = new File(FILEPATH+FILENAME);
        writer = new FileWriter(mainFile);
        textComp.write(writer);
        writer.close();
        
  	   //File inputFile = new File(FILEPATH);
  	   byte[] outputByte =  CryptoUtil.encrypt(KEY, mainFile);
  	   FileOutputStream outputStream = new FileOutputStream(mainFile);
  	   outputStream.write(outputByte);
  	   outputStream.close();
      }
      catch (IOException ex) {
        JOptionPane.showMessageDialog(SimpleEditor.this,
        "File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
      }catch(Exception e){
    	  e.printStackTrace();
      }
      finally {
        if (writer != null) {
          try {
            writer.close();
          } catch (IOException x) {}
        }
      }
    }
  }
}