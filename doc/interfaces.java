interface Bot {
	/**
	 * Sends a message to some user and replies whatever user responses.
	 *
	 * @param who     the user to send the message to
	 * @param message the message to send
	 * @return whatever user responses
	 */
	String sendMessage(String who, String message);
}

interface NLPBot extends Bot {
	/**
	 * Trains the NLP bot to recognize the expected answers to some specific question.
	 *
	 * @param question          the question to ask
	 * @param expectedAnswers the expected answer set
	 */
	void trainBot(String question, String[] expectedAnswers);
}

interface WorkflowService {
	/**
	 * Initilaizes a workflow context
	 *
	 * @param flowType the flow type to initialize
	 * @param metadata the runtime data required to kick off this specific flow. It must contain all placeholder values in the corresponding workflow config.
	 * @return a unique context ID of the running workflow
	 */
	String startWorkflow(String flowType, Map<String, Object> metadata);

	/**
	 * Moves to next workflow state by the given transition
	 *
	 * @param contextId  the context ID of the running workflow to manipulate on
	 * @param transition the transition which determines the next state
	 * @return A tuple of the next question and whom to ask
	 */
	Tuple<String, String> nextState(String contextId, String transition);
}

