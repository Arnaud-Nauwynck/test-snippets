package fr.an.dynadapter;

public interface IDynAdaptable<IId> {

    // public <T> T getAdapter(IId adapter, Class<T> clss);
    public Object getAdapter(IId adapter);

}