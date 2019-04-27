package com.dk.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 非web应用程序SpringBoot启动
 */
@EnableTransactionManagement  //事务自动扫描
@SpringBootApplication
@EnableScheduling //开启定时任务
@EnableCaching // 启动缓存
@ImportResource({"classpath:applicationContext-dubbox-provider.xml"})
@ComponentScan(basePackages={"com.dk"})
@MapperScan("com.dk.provider.*.mapper")
public class DkProviderApplication implements CommandLineRunner {




   public static void main(String[] args) {
       // 不占用端口启动
       new SpringApplicationBuilder(DkProviderApplication.class)
               .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
               .run(args);
   }

    /* <p>让主线程阻塞让程序不退出</p>
     * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
     */
    @Override
    public void run (String... args) throws Exception
    {
        //Thread.currentThread().join();
        System.out.println("-----------------------provider启动成功--------------------------------");
    }

}


