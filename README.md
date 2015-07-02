# mooc-mining
Compares the frequency of words from some source to the frequency of all academic texts in the COCA (Corpus of Contemporary American English).  More information about the COCA project may be found here: http://www.academicvocabulary.info/.  For more about COCA projects, created by Mark Davies at Brigham Young University, check out http://corpus.byu.edu/.

## Importing this Project
This project uses Eclipse.  To import this project into your Eclipse workspace, do the following:

1. In Eclipse, open the **Import...** wizard, choose **Git -> Projects from Git**, and then click **Next >**
2. On the next page, choose **Clone URI**
3. Copy the *HTTPS clone URL* for this repository.  All of the other necessary fields should fill in automatically.  Click **Next >**.
4. Click **Next >** on the *Branch Selection* page.
5. On the *Local Destination* page, you may choose the root directory for the local copy of the repo.
6. **Import existing projects** on the *Select a wizard to use...* page.
7. Click **Finish**.  The project will now appear in your workspace.  Any git-related operations (pull, commit, etc.) can be found under the *Team* context menu by right-clicking on your project.


## Dependencies
This project uses Maven for dependency management.  If you don't have the Maven Eclipse plug-in, you may install it from the following Eclipse repo location: http://download.eclipse.org/technology/m2e/releases/

Dependencies used:

| Name + Website  | Version | Description |
| ---             | ---     | ---         |
| Weka ([API documentation](https://weka.wikispaces.com/Use+WEKA+in+your+Java+code)) | 3.6.6 | Machine Learning/Classification Toolbox |
| POI  ([site](https://poi.apache.org/)) ([Excel example](https://poi.apache.org/spreadsheet/converting.html)) | 3.7      | API for Microsoft documents |
