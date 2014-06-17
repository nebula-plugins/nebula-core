package nebula.core

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Namer
import org.gradle.api.Project
import org.gradle.api.internal.DynamicPropertyNamer
import org.gradle.api.internal.FactoryNamedDomainObjectContainer
import org.gradle.api.internal.project.AbstractProject
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

public class NamedContainerProperOrder<T> extends FactoryNamedDomainObjectContainer<T> {

    public static <C> NamedContainerProperOrder<C> container(Project p, Class<C> type) {
        Instantiator instantiator = ((AbstractProject) p).getServices().get(Instantiator.class);
        return instantiator.newInstance(NamedContainerProperOrder.class, type, instantiator, new DynamicPropertyNamer());
    }

    @Override
    public T create(String name, Closure configureClosure) {
        assertCanAdd(name);
        T object = doCreate(name);
        // Configure the object BEFORE, adding and kicking off addEvents in doAdd
        ConfigureUtil.configure(configureClosure, object);
        add(object);
        return object;
    }

    // @groovy.transform.InheritConstructors doesn't work with this class, so copying them here.
    /**
     * <p>Creates a container that instantiates reflectively, expecting a 1 arg constructor taking the name.<p>
     *
     * <p>The type must implement the {@link org.gradle.api.Named} interface as a {@link org.gradle.api.Namer} will be created based on this type.</p>
     *
     * @param type The concrete type of element in the container (must implement {@link org.gradle.api.Named})
     * @param instantiator The instantiator to use to create any other collections based on this one
     */
    public NamedContainerProperOrder(Class<T> type, Instantiator instantiator) {
        super(type, instantiator)
    }

    /**
     * <p>Creates a container that instantiates reflectively, expecting a 1 arg constructor taking the name.<p>
     *
     * @param type The concrete type of element in the container (must implement {@link org.gradle.api.Named})
     * @param instantiator The instantiator to use to create any other collections based on this one
     * @param namer The naming strategy to use
     */
    public NamedContainerProperOrder(Class<T> type, Instantiator instantiator, Namer<? super T> namer) {
        super(type, instantiator, namer)
    }

    /**
     * <p>Creates a container that instantiates using the given factory.<p>
     *
     * @param type The concrete type of element in the container (must implement {@link org.gradle.api.Named})
     * @param instantiator The instantiator to use to create any other collections based on this one
     * @param factory The factory responsible for creating new instances on demand
     */
    public NamedContainerProperOrder(Class<T> type, Instantiator instantiator, NamedDomainObjectFactory<T> factory) {
        super(type, instantiator, factory)
    }

    /**
     * <p>Creates a container that instantiates using the given factory.<p>
     *
     * @param type The concrete type of element in the container
     * @param instantiator The instantiator to use to create any other collections based on this one
     * @param namer The naming strategy to use
     * @param factory The factory responsible for creating new instances on demand
     */
    public NamedContainerProperOrder(Class<T> type, Instantiator instantiator, Namer<? super T> namer, NamedDomainObjectFactory<T> factory) {
        super(type, instantiator, namer, factory)
    }

    /**
     * <p>Creates a container that instantiates using the given factory.<p>
     *
     * @param type The concrete type of element in the container (must implement {@link org.gradle.api.Named})
     * @param instantiator The instantiator to use to create any other collections based on this one
     * @param factoryClosure The closure responsible for creating new instances on demand
     */
    public NamedContainerProperOrder(Class<T> type, Instantiator instantiator, final Closure factoryClosure) {
        super(type, instantiator, factoryClosure)
    }

}
