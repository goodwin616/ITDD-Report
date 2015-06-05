import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class appView {

	class TabHolder {
		private String name;
		private ArrayList<QGroup> questions;
		
		public TabHolder(String name, ArrayList<QGroup> questions) {
			this.name = name;
			this.questions = questions;
		}
		public String getName() {return name;}
		public ArrayList<QGroup> getQuestions() {return questions;}
	}
	
	class QGroup {
		private String question;
		private ArrayList<answerSelections> answers;
		
		public QGroup(String question, ArrayList<answerSelections> answers) {
			this.question = question;
			this.answers = answers;
		}
		public String getQuestion() {return question;}
		public ArrayList<answerSelections> getAnswers() {return answers;}
	}
	
	class answerSelections {
		private String answer;
		private String fillIn;
		private Button button;
		
		public answerSelections(String answer, String fillIn) {
			this.answer = answer;
			this.fillIn= fillIn;
			this.button = null;
		}
		public String getAnswer() {return answer;}
		public String getfillIn() {return fillIn;}
		public Button getButton() {return button;}
		
		public void setButton(Button button) {this.button = button;}
	}
	
	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			appView window = new appView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(480, 534);
		shell.setText("ITDD");
		System.out.println("Start parse");
		ArrayList<TabHolder> tabList = parseInput();
		System.out.println("End parse");

		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(0, 10, 464, 455);
		
		for (int i = 0; i < tabList.size(); i++) {
			TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
			tabItem_1.setText(tabList.get(i).getName());
			ArrayList<QGroup> questionList = tabList.get(i).getQuestions();
			Composite composite = new Composite(tabFolder, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));

			for(int j = 0; j < questionList.size(); j++) {
				Group group = new Group(composite, SWT.NONE);
				GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_group.widthHint = 441;
				group.setLayoutData(gd_group);
				group.setText(questionList.get(j).getQuestion());
				group.setLayout(new GridLayout(1, false));
				
				for(int k = 0; k < questionList.get(j).getAnswers().size(); k++) {
					Button button = new Button(group, SWT.RADIO);
					button.setText(questionList.get(j).getAnswers().get(k).getAnswer());
					tabList.get(i).getQuestions().get(j).getAnswers().get(k).setButton(button);
				}
			}
			tabItem_1.setControl(composite);
		}

		Button btnCreateDocument = new Button(shell, SWT.NONE);
		btnCreateDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < tabList.size(); i++) {
					int questionSize = tabList.get(i).getQuestions().size();
					for (int j = 0; j < questionSize; j++) {
						int answerSize = tabList.get(i).getQuestions().get(j).getAnswers().size();
						for(int k = 0; k < answerSize; k++) {
							if (tabList.get(i).getQuestions().get(j).getAnswers().get(k).getButton().getSelection()) {
								System.out.println(tabList.get(i).getQuestions().get(j).getAnswers().get(k).getfillIn());
							}
						}
					}
				}
				
			}
		});
		btnCreateDocument.setBounds(326, 471, 128, 25);
		btnCreateDocument.setText("Create Document");	
	}
	
	public ArrayList<TabHolder> parseInput() {
		String filename = "test1.txt";
		File file = new File(filename);
		try {
			// Read in text file
			FileReader reader = new FileReader(file);
			BufferedReader textReader = new BufferedReader(reader);
			String line = textReader.readLine();
			if (line == null) 
				System.exit(0);
			// ...until there is nothing else to read
			ArrayList<TabHolder> retVal = new ArrayList<TabHolder>();

			while(!line.equals("END")) {	
				//start tab
				if (line.equals("*--")) {
					line = textReader.readLine();
					TabHolder tab = new TabHolder(line, new ArrayList<QGroup>());
					line = textReader.readLine();
					while (!line.equals("*--")) {
						//if true start new question
						if (line.equals("---")) {
							System.out.println("question");
							line = textReader.readLine();
							QGroup g = new QGroup(line, new ArrayList<answerSelections>());
							line = textReader.readLine();
							while(!line.equals("^^^")) {
								String answer = line;
								line = textReader.readLine();
								String fillIn = line;
								g.getAnswers().add(new answerSelections(answer, fillIn));
								line = textReader.readLine();
							}
							tab.getQuestions().add(g);
							line = textReader.readLine();		
						}
					}
					retVal.add(tab);
				}
				line = textReader.readLine();
			}
			return retVal;
		}	
		catch (IOException x) {
		    System.err.println(x);
		}
		
		return null; 
	}
}
