package org.neo4j.spring.example.proteins;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world(s)!
 * <p/>
 * An example application for exploring Spring Data Graph.
 */
public class Main
{

    public static void main( String[] args ) throws Exception
    {
        ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "/spring/proteinsContext.xml" );
        
        ProteinClassRepository classRepo = applicationContext.getBean( ProteinClassRepository.class );

        Iterable<ProteinClass> proteinClasses = classRepo.createProteinClasses();

        for ( ProteinClass clazz : proteinClasses )
            System.out.println( "At home on: " + clazz );

        applicationContext.close();
    }
}
