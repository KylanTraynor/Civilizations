package com.kylantraynor.civilizations.questions;

import java.util.HashMap;
import java.util.Map;

public class QuestionsHandler {
	
	static Map<Object, Question> questions = new HashMap<Object, Question>();

	public static void registerGroupQuestion(GroupQuestion q){
		if(questions.containsKey(q.getGroup())){
			killQuestion(q.getGroup());
		}
		questions.put(q.getGroup(), q);
	}
	
	public static void registerQuestion(Question q){
		if(questions.containsKey(q.getSender())){
			killQuestion(q.getSender());
		}
		questions.put(q.getSender(), q);
	}
	public static void killQuestion(Object s){
		questions.remove(s);
	}
	public static Question getQuestion(Object s)
	{
		return (Question)questions.get(s);
	}
}
