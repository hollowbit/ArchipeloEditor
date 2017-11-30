package net.hollowbit.archipeloeditor.tools;

public class Wrapper<T> {
	
	private T object;

	public Wrapper() {
		super();
	}
	
	public Wrapper(T object) {
		super();
		this.object = object;
	}

	public T get() {
		return object;
	}

	public void set(T value) {
		this.object = value;
	}
	
}
