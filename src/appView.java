import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ParagraphFormat;


@SuppressWarnings("unused")
public class appView {

	final Display display = Display.getDefault();

	
	class TabHolder {
		private String name;
		private ArrayList<QGroup> questions;

		public TabHolder(String name, ArrayList<QGroup> questions) {
			this.name = name;
			this.questions = questions;
		}

		public String getName() {
			return name;
		}

		public ArrayList<QGroup> getQuestions() {
			return questions;
		}
	}

	class QGroup {
		private String question;
		private ArrayList<answerSelections> answers;

		public QGroup(String question, ArrayList<answerSelections> answers) {
			this.question = question;
			this.answers = answers;
		}

		public String getQuestion() {
			return question;
		}

		public ArrayList<answerSelections> getAnswers() {
			return answers;
		}
	}

	class answerSelections {
		private String answer;
		private String fillIn;
		private Button button;

		public answerSelections(String answer, String fillIn) {
			this.answer = answer;
			this.fillIn = fillIn;
			this.button = null;
		}

		public String getAnswer() {
			return answer;
		}

		public String getfillIn() {
			return fillIn;
		}

		public Button getButton() {
			return button;
		}

		public void setButton(Button button) {
			this.button = button;
		}
	}

	protected Shell shell;
	private Text txtOutputdocx;

	/**
	 * Launch the application.
	 * 
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
		shell.setSize(480, 616);
		shell.setText("ITDD");
		System.out.println("Parsing test1.txt");
		ArrayList<TabHolder> tabList = parseInput();
		System.out.println("End parse, Launching ITDD Document Creator");
		
		TabFolder tabFolder = new TabFolder(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		tabFolder.setBounds(0, 10, 464, 527);

		for (int i = 0; i < tabList.size(); i++) {

			ArrayList<QGroup> questionList = tabList.get(i).getQuestions();

			ScrolledComposite scroll = new ScrolledComposite(tabFolder,
					SWT.V_SCROLL | SWT.BORDER);
			Composite composite = new Composite(scroll, SWT.NONE);

			scroll.setContent(composite);
			scroll.setExpandHorizontal(true);
			scroll.setExpandVertical(true);
			scroll.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					Rectangle r = scroll.getClientArea();
					scroll.setMinSize(composite.computeSize(r.width,
							SWT.DEFAULT));
				}
			});

			composite.setLayout(new GridLayout(1, false));

			for (int j = 0; j < questionList.size(); j++) {
				Group group = new Group(composite, SWT.NONE);
				GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false,
						false, 1, 1);
				gd_group.widthHint = 415;
				group.setLayoutData(gd_group);
				group.setText(questionList.get(j).getQuestion());
				group.setLayout(new GridLayout(1, false));
				int numAns = questionList.get(j).getAnswers().size();
				for (int k = 0; k < numAns; k++) {
					Button button = new Button(group, SWT.RADIO);
					button.setText(questionList.get(j).getAnswers().get(k)
							.getAnswer());
					if (k == numAns-1) {
						button.setSelection(true);
					}
					tabList.get(i).getQuestions().get(j).getAnswers().get(k)
							.setButton(button);
				}
			}

			// Expand both horizontally and vertically

			TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
			tabItem_1.setText(tabList.get(i).getName());
			tabItem_1.setControl(scroll);
		}

		Button btnCreateDocument = new Button(shell, SWT.NONE);
		btnCreateDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					createDocument(tabList, txtOutputdocx.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				for (int i = 0; i < tabList.size(); i++) {
					int questionSize = tabList.get(i).getQuestions().size();
					for (int j = 0; j < questionSize; j++) {
						int answerSize = tabList.get(i).getQuestions().get(j)
								.getAnswers().size();
						for (int k = 0; k < answerSize; k++) {
							if (tabList.get(i).getQuestions().get(j)
									.getAnswers().get(k).getButton()
									.getSelection()) {
								System.out.println("For Question: "
										+ tabList.get(i).getQuestions().get(j)
												.getQuestion()
										+ ", you selected "
										+ tabList.get(i).getQuestions().get(j)
												.getAnswers().get(k)
												.getAnswer()
										+ " which corresponds to ... "
										+ tabList.get(i).getQuestions().get(j)
												.getAnswers().get(k)
												.getfillIn());
							}
						}
					}
				}

			}
		});
		btnCreateDocument.setBounds(326, 543, 128, 25);
		btnCreateDocument.setText("Create Document");
		
		txtOutputdocx = new Text(shell, SWT.BORDER);
		txtOutputdocx.setText("output");
		txtOutputdocx.setToolTipText("w/o extension");
		txtOutputdocx.setBounds(73, 545, 240, 21);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 548, 55, 15);
		lblNewLabel.setText("File Name");
	}

	public void createDocument(ArrayList<TabHolder> tabList, String docName) throws Exception {
		
		Document doc = new Document(); 
		DocumentBuilder builder = new DocumentBuilder(doc);
		ParagraphFormat paragraphFormat = builder.getParagraphFormat(); 
		paragraphFormat.setFirstLineIndent(8);
		paragraphFormat.setAlignment(ParagraphAlignment.JUSTIFY);
		
		for (int i = 0; i < tabList.size(); i++) {
			int questionSize = tabList.get(i).getQuestions().size();
			for (int j = 0; j < questionSize; j++) {
				int answerSize = tabList.get(i).getQuestions().get(j)
						.getAnswers().size();
				for (int k = 0; k < answerSize; k++) {
					if (tabList.get(i).getQuestions().get(j).getAnswers()
							.get(k).getButton().getSelection()) {
						
						
						builder.write(tabList.get(i).getQuestions().get(j).getAnswers()
								.get(k).getfillIn());
					}
				}
			}
			builder.writeln();
		}
		
		
		// Document Name Data Validation
		String retVal = "";
		String[] words = docName.split(Pattern.quote("."));
		if(words[words.length-1].equalsIgnoreCase("docx") || words[words.length-1].equalsIgnoreCase("doc")) {
			String newDocName = "";
			for (int i = 0; i < words.length-1; i++) {
				newDocName = newDocName + words[i] + ".";
			}
			newDocName += "docx";
			doc.save(newDocName);
			retVal = newDocName;
		}
		else {
	        doc.save(docName + ".docx");
	        retVal = docName + ".docx";
		}
		
		MessageBox dialog =  new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
			dialog.setText("ITDD Report");
			dialog.setMessage("Document Created with name: " + retVal + "\n\nDo you want to close the application? Press OK");

				// open dialog and await user selection
		int ret = dialog.open();
		System.out.println(ret);
		if (ret == 32) {
			System.exit(0);
		}
		return;
	}

	public ArrayList<TabHolder> parseInput() {
		String filename = "test1.txt";
		File file = new File(filename);
		try {
			// Read in text file
			FileReader reader = new FileReader(file);
			@SuppressWarnings("resource")
			BufferedReader textReader = new BufferedReader(reader);
			String line = textReader.readLine();
			if (line == null)
				System.exit(0);
			// ...until there is nothing else to read
			ArrayList<TabHolder> retVal = new ArrayList<TabHolder>();

			while (!(line.equals("END") || line.equals(null))) {
				// start tab
				if (line.equals("*--")) {
					line = textReader.readLine();
					TabHolder tab = new TabHolder(line, new ArrayList<QGroup>());
					line = textReader.readLine();
					while (!line.equals("*--")) {
						// if true start new question
						if (line.equals("---")) {
							System.out.println("question");
							line = textReader.readLine();
							QGroup g = new QGroup(line,
									new ArrayList<answerSelections>());
							line = textReader.readLine();
							while (!line.equals("^^^")) {
								String answer = line;
								line = textReader.readLine();
								String fillIn = line;
								g.getAnswers().add(
										new answerSelections(answer, fillIn));
								line = textReader.readLine();
							}
							g.getAnswers().add(new answerSelections("Not Applicable", ""));
							tab.getQuestions().add(g);
							line = textReader.readLine();
						}
					}
					retVal.add(tab);
				}
				line = textReader.readLine();
			}
			return retVal;
		} catch (IOException x) {
			System.err.println(x);
		}

		return null;
	}
}
