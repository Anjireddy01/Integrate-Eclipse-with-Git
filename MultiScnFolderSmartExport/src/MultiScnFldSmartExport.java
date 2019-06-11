//package src.oracle.odi.Exelon.scnLpFolder;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.ITransactionCallback;
import oracle.odi.core.persistence.transaction.support.TransactionTemplate;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.impexp.EncodingOptions;
import oracle.odi.impexp.smartie.ISmartExportService;
import oracle.odi.impexp.smartie.ISmartExportable;
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl;
import oracle.odi.publicapi.samples.SimpleOdiInstanceHandle;
import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;
import oracle.odi.domain.project.IOdiScenarioSourceContainer;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder;
import oracle.odi.domain.runtime.scenario.OdiScenarioFolder;

public class MultiScnFldSmartExport 
{
 
     private static String Project_Code;
     private static OdiProject project;
     private static String   Folder_Name;
     private static OdiFolder folder;

    public static void main(String[] args) throws IOException, ParseException 
	{

     String Url =           args[0];
     String Driver =        args[1];
     String Master_User =   args[2];
     String Master_Pass =   args[3];
     String WorkRep =       args[4];
     String Odi_User =      args[5];
     String Odi_Pass =      args[6];
     String ExportFolderPath = args[9];
     char[] ExportKey = "P@ssw0rd".toCharArray(); 
     final String smartExportFileName = args[8];
     String scnFolder = args[7];
     Boolean ExportPackageScen      = true;
     Boolean ExportInterfaceScen    = true;
     Boolean ExportProcedureScen    = true;
     Boolean ExportVariableScen     = false;
     Boolean RecursiveExport        = true;
     Boolean OverWriteFile          = true;
     Boolean ExportWithoutCipherData  = false;
 
 
 
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Date date =df.parse("10-09-2018");

		MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(Url, Driver, Master_User,Master_Pass.toCharArray(), new PoolingAttributes());

		WorkRepositoryDbInfo workInfo = new WorkRepositoryDbInfo(WorkRep, new PoolingAttributes());
		OdiInstance odiInstance=OdiInstance.createInstance(new OdiInstanceConfig(masterInfo,workInfo));
		Authentication auth = odiInstance.getSecurityManager().createAuthentication(Odi_User,Odi_Pass.toCharArray());
		odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);
		ITransactionStatus trans = odiInstance.getTransactionManager().getTransaction(new DefaultTransactionDefinition());
		
		System.out.println( " Successfully COnnected to ODI Work Repository");
        
		final LinkedList<String> relatedObjectsTags = new LinkedList<String> ();
        relatedObjectsTags.add ("");
		System.out.println( " relatedOBgectsTags");

        final EncodingOptions expeo = new EncodingOptions ("1.0", "ISO8859_9",  "ISO-8859-9");
        
        Locale locale = new Locale ("en", "US");
        Locale.setDefault (locale);
        final List<ISmartExportable> expIntegrationInterfaces = new LinkedList<ISmartExportable> ();        
        
        TransactionTemplate transaction = new TransactionTemplate (odiInstance.getTransactionManager());

		System.out.println( "Transactions");

        transaction.execute (new ITransactionCallback ()
            {
                public Object doInTransaction(ITransactionStatus pStatus)
                {
						Collection<OdiScenarioFolder> scenariosfld = ((IOdiScenarioFolderFinder) 
			odiInstance.getTransactionalEntityManager().getFinder(OdiScenario.class)).findAll();
			System.out.println( "scenariosfld >>>>>>"+scenariosfld);
				for(OdiScenarioFolder scen:scenariosfld)
				{
				//System.out.print(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+scen.getName());
				//expIntegrationInterfaces.add( (ISmartExportable) scen);
				}
				//OdiScenario fold = ((IOdiScenarioFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiScenario.class)).findByName(scnFolder);
				StringTokenizer planTokens=new StringTokenizer(args[7],";");
				while(planTokens.hasMoreTokens())
				{
				OdiScenarioFolder fold = ((IOdiScenarioFolderFinder) 
				odiInstance.getTransactionalEntityManager().getFinder(OdiScenarioFolder.class)).findByName(planTokens.nextToken());
				expIntegrationInterfaces.add( (ISmartExportable) fold);
				}
					//OdiScenarioFolder fold = ((IOdiScenarioFolderFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiScenarioFolder.class)).findByName(scnFolder);                             
					System.out.println( "defining.........");
                    //expIntegrationInterfaces.add( (ISmartExportable) scen);
					System.out.println( "define smart export method");
                    ISmartExportService esvc = new SmartExportServiceImpl(odiInstance);
                    System.out.println( "export method defineing done");
                    try 
					{
                        esvc.exportToXml (expIntegrationInterfaces, ExportFolderPath, smartExportFileName, true, false, expeo, false, null, ExportKey, ExportWithoutCipherData);
                    System.out.println( "Smart Export has been Completed");
                    } 
					catch (IOException e)
					{
                        e.printStackTrace ();
                    }
        
                    return null;
                }



}


);

	
	}
}	


