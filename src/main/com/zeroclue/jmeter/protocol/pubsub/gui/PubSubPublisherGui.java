package com.zeroclue.jmeter.protocol.pubsub.gui;

import java.awt.Dimension;

import javax.swing.*;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;

import com.zeroclue.jmeter.protocol.pubsub.PubSubPublisher;

public class PubSubPublisherGui extends PubSubSamplerGui {

    private static final long serialVersionUID = 1L;

    private JPanel mainPanel;

    private JLabeledTextField projectId = new JLabeledTextField("Project Id");
    private JLabeledTextField topicName = new JLabeledTextField("Topic Name");
    private JLabeledTextArea message = new JLabeledTextArea("Message Content");

    public PubSubPublisherGui(){
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getStaticLabel() {
        return "PubSub Publisher";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof PubSubPublisher)) return;
        PubSubPublisher sampler = (PubSubPublisher) element;

        projectId.setText(sampler.getProjectId());
        topicName.setText(sampler.getTopicName());
        message.setText(sampler.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestElement createTestElement() {
        PubSubPublisher sampler = new PubSubPublisher();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement te) {
        PubSubPublisher sampler = (PubSubPublisher) te;
        sampler.clear();
        configureTestElement(sampler);

        super.modifyTestElement(sampler);

        sampler.setProjectId(projectId.getText());
        sampler.setTopicName(topicName.getText());

        sampler.setMessage(message.getText());
    }

    @Override
    protected void setMainPanel(JPanel panel){
        mainPanel = panel;
    }

    /*
     * Helper method to set up the GUI screen
     */
    @Override
    protected final void init() {
        super.init();

        projectId.setPreferredSize(new Dimension(100, 25));
        topicName.setPreferredSize(new Dimension(100, 25));
        message.setPreferredSize(new Dimension(400, 150));

        mainPanel.add(projectId);
        mainPanel.add(topicName);
        mainPanel.add(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        super.clearGui();
        projectId.setText("");
        topicName.setText("");
        message.setText("");
    }
}