# Annotations

In order to test a demo, get demo.zip in sub-directiry Demo,
Unzip it somewhere.

Now in oxygen, import the scenario as follows :

    click on the dedicated tool (the wrench with a red triangle)
    in the windows that opens, click on the gears and then on "import scenario"
    select demoOxygenScenario.scenarios
   

This should result in a scenario called "demo tagging"
Edit the scenario and modify the "build file", navigate to where you unziped demo.zip and choose the file : buildAnn.xml

Then open an XML document (for instance ST-Infrastructure.xml that is stored in the demo.zip).

If you apply the scenario (demo tagging) to this XLM file, after a while, a new tab should be created that contains the POS tagged version of the XML file. 
