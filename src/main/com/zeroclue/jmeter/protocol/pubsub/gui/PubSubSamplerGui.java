package com.zeroclue.jmeter.protocol.pubsub.gui;

import com.zeroclue.jmeter.protocol.pubsub.PubSubSampler;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.swing.*;
import java.awt.*;

public abstract class PubSubSamplerGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    protected JLabeledTextField timeout = new JLabeledTextField("Timeout");
    private final JLabeledTextField iterations = new JLabeledTextField("Number of samples to Aggregate");

    protected abstract void setMainPanel(JPanel panel);

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof PubSubSampler)) return;
        PubSubSampler sampler = (PubSubSampler) element;

        timeout.setText(sampler.getTimeout());
        iterations.setText(sampler.getIterations());
        log.info("PubSubSamplerGui.configure() called");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        timeout.setText(PubSubSampler.DEFAULT_TIMEOUT_STRING);
        iterations.setText(PubSubSampler.DEFAULT_ITERATIONS_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement element) {
        PubSubSampler sampler = (PubSubSampler) element;
        sampler.clear();
        configureTestElement(sampler);

        sampler.setTimeout(timeout.getText());
        sampler.setIterations(iterations.getText());

        log.info("PubSubSamplerGui.modifyTestElement() called");
    }

    protected void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title

        JPanel mainPanel = new VerticalPanel();

        mainPanel.add(makeCommonPanel());

        iterations.setPreferredSize(new Dimension(50,25));
        mainPanel.add(iterations);

        add(mainPanel);

        setMainPanel(mainPanel);
    }

    private Component makeCommonPanel() {
        JPanel commonPanel = new JPanel(new GridBagLayout());
        return commonPanel;
    }

}
