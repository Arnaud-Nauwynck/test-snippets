package fr.an.eadapter;

public interface IAdaptable {

    public <T> T getAdapter(Class<T> adapter);
    
}