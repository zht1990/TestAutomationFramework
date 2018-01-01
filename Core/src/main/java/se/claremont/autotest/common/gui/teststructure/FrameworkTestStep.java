package se.claremont.autotest.common.gui.teststructure;

public class FrameworkTestStep extends TestStep {

    public FrameworkTestStep(){}

    public FrameworkTestStep(String name, String description){
        super(name, description);
    }

    @Override
    public String asCode() {
        return null;
    }

    @Override
    public TestStep clone() {
        FrameworkTestStep clonedStep = new FrameworkTestStep(getName(), getDescription());
        clonedStep.setActionName(actionName);
        clonedStep.setElementName(elementName);
        clonedStep.setAssociatedData(data);
        return clonedStep;
    }

    @Override
    public void setActionName(String actionName) {

    }

    @Override
    public void setElementName(String elementName) {

    }

    @Override
    public void setAssociatedData(Object data) {

    }

    @Override
    public String getTestStepTypeShortName() {
        return "TAF";
    }

    @Override
    public TestStepResult execute() {
        return null;
    }
}