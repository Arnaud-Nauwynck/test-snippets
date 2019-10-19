
Test Maven LifeCycle
----------------


populatePluginFields(Object, MojoDescriptor, ClassRealm, PlexusConfiguration, ExpressionEvaluator) : void - org.apache.maven.plugin.internal.DefaultMavenPluginManager
    getConfiguredMojo(Class<T>, MavenSession, MojoExecution) <T> : T - org.apache.maven.plugin.internal.DefaultMavenPluginManager
        executeMojo(MavenSession, MojoExecution) : void - org.apache.maven.plugin.DefaultBuildPluginManager
        getConfiguredMavenReport(MojoExecution, PluginDescriptor, MavenReportExecutorRequest) : MavenReport - org.apache.maven.reporting.exec.DefaultMavenReportExecutor
        
        

    // DefaultMojoExecutionConfigurator
    @Override
    public void configure( MavenProject project, MojoExecution mojoExecution, boolean allowPluginLevelConfig )
    {
        Plugin plugin = findPlugin( g, a, project.getBuildPlugins() );
        if ( plugin == null && project.getPluginManagement() != null ) {
            plugin = findPlugin( g, a, project.getPluginManagement().getPlugins() );
        }
        PluginExecution pluginExecution =
            findPluginExecution( mojoExecution.getExecutionId(), plugin.getExecutions() );

            Xpp3Dom pomConfiguration = null;
            if ( pluginExecution != null ) {
                pomConfiguration = (Xpp3Dom) pluginExecution.getConfiguration();
            } else if ( allowPluginLevelConfig ) {
                pomConfiguration = (Xpp3Dom) plugin.getConfiguration();
            }
            Xpp3Dom mojoConfiguration = ( pomConfiguration != null ) ? new Xpp3Dom( pomConfiguration ) : null;
            mojoConfiguration = Xpp3Dom.mergeXpp3Dom( mojoExecution.getConfiguration(), mojoConfiguration );
            mojoExecution.setConfiguration( mojoConfiguration );



            // org.apache.maven.plugin.internal.DefaultMavenPluginManager

            ExpressionEvaluator expressionEvaluator = new PluginParameterExpressionEvaluator( session, mojoExecution );

            populatePluginFields( mojo, mojoDescriptor, pluginRealm, pomConfiguration, expressionEvaluator );



        ComponentConfigurator configurator = null;
        String configuratorId = mojoDescriptor.getComponentConfigurator();
        configurator = container.lookup( ComponentConfigurator.class, configuratorId );
            
        ConfigurationListener listener = new DebugConfigurationListener( logger );
        ValidatingConfigurationListener validator =
                new ValidatingConfigurationListener( mojo, mojoDescriptor, listener );

        configurator.configureComponent( mojo, configuration, expressionEvaluator, pluginRealm, validator );
       
       // => org.codehaus.plexus.component.configurator.BasicComponentConfigurator     
            new ObjectWithFieldsConverter().processConfiguration( converterLookup, component, realm, configuration, evaluator, listener );
            
            
            CompositeBeanHelper helper = new CompositeBeanHelper( lookup, loader, evaluator, listener );

            final PlexusConfiguration element = configuration.getChild( i );
            final String propertyName = fromXML( element.getName() );
            Class<?> valueType = getClassForImplementationHint( null, element, loader );
            helper.setProperty( bean, propertyName, valueType, element );
            
            
            Method setter = findMethod( beanType, paramTypeHolder, "set" + title );
            if ( null == setter ) setter = findMethod( beanType, paramTypeHolder, "add" + title );
            final Field field = findField( beanType, propertyName );
        
            value = convertProperty( beanType, rawPropertyType, paramType.getType(), configuration );
            listener.notifyFieldChangeUsingSetter( propertyName, value, bean );
            setter.invoke( bean, value );
            
            listener.notifyFieldChangeUsingReflection( propertyName, value, bean );
            setField( bean, field, value );
            
            