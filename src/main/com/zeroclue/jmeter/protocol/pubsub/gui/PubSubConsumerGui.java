package com.zeroclue.jmeter.protocol.pubsub.gui;

import com.zeroclue.jmeter.protocol.pubsub.PubSubConsumer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;

import javax.swing.*;
import java.awt.*;


public class PubSubConsumerGui extends PubSubSamplerGui {

    private static final long serialVersionUID = 1L;

    protected JLabeledTextField projectId = new JLabeledTextField("Project Id");
    protected JLabeledTextField subscriptionName = new JLabeledTextField("Subscription Name");

    private JPanel mainPanel;

    public PubSubConsumerGui(){
        init();
    }

    /*
     * Helper method to set up the GUI screen
     */
    protected void init() {
        super.init();

        projectId.setPreferredSize(new Dimension(100, 25));
        subscriptionName.setPreferredSize(new Dimension(100, 25));

        mainPanel.add(projectId);
        mainPanel.add(subscriptionName);
    }

    @Override
    public String getStaticLabel() {
        return "PubSub Consumer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof PubSubConsumer)) return;
        PubSubConsumer sampler = (PubSubConsumer) element;

        projectId.setText(sampler.getProjectId());
        subscriptionName.setText(sampler.getSubscriptionName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        super.clearGui();
        projectId.setText("");
        subscriptionName.setText("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestElement createTestElement() {
        PubSubConsumer sampler = new PubSubConsumer();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement te) {
        PubSubConsumer sampler = (PubSubConsumer) te;
        sampler.clear();
        configureTestElement(sampler);

        super.modifyTestElement(sampler);

        sampler.setProjectId(projectId.getText());
        sampler.setSubscriptionName(subscriptionName.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void setMainPanel(JPanel panel) {
        mainPanel = panel;
    }


}
