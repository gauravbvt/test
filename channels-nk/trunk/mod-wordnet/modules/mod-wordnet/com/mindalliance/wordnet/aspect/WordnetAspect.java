package com.mindalliance.wordnet.aspect;

import com.mindalliance.wordnet.Wordnet;

public class WordnetAspect implements IAspectWordnet {
	private Wordnet t;

	public WordnetAspect(Wordnet t) {
		this.t = t;
	}

	public Wordnet getWordnet() {
		return t;
	}
}
