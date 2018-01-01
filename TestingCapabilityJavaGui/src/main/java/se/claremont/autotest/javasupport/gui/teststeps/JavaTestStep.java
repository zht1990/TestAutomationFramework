package se.claremont.autotest.javasupport.gui.teststeps;

import se.claremont.autotest.common.gui.teststructure.SubProcedureTestStep;
import se.claremont.autotest.common.gui.teststructure.TestCaseManager;
import se.claremont.autotest.common.gui.teststructure.TestStep;
import se.claremont.autotest.common.gui.teststructure.TestStepResult;

public class JavaTestStep extends TestStep {

    public JavaTestStep(){
        super();
    }

    public JavaTestStep(String name, String description){
        super(name, description);
    }

    @Override
    public String asCode() {
        TestCaseManager.testSetCode.makeSureRequiredImportIsAdded("import se.claremont.autotest.javasupport.interaction.*;");
        TestCaseManager.testSetCode.makeSureClassVariableIsDeclared("GenericInteractionMethods java;");
        TestCaseManager.testSetCode.makeSureBeginTestSectionDeclarationExist("java = new GenericInteractionMethods(currentTestCase());");
        return null;
    }

    @Override
    public JavaTestStep clone() {
        JavaTestStep clonedStep = new JavaTestStep(getName(), getDescription());
        clonedStep.setActionName(actionName);
        clonedStep.setElementName(elementName);
        clonedStep.setAssociatedData(data);
        return clonedStep;
    }

    @Override
    public String getTestStepTypeShortName() {
        return "Java";
    }

    @Override
    public TestStepResult execute() {
        return new TestStepResult(this, TestStepResult.Result.NOT_RUN);
    }
}