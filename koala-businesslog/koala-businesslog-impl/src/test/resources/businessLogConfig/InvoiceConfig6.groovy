package businessLogConfig

import business.ContractApplication
import org.dayatang.domain.InstanceFactory

/**
 * User: zjzhai
 * Date: 3/6/14
 * Time: 2:32 PM
 */
class InvoiceConfig6 {

    def context

    def InvoiceApplicationImpl_addInvoice8() {
        ContractApplication contractApplication =
            InstanceFactory.getInstance(ContractApplication.class, "contractApplication")

        String xx = contractApplication.addContract(22)

        [category: "发票",
                log : "add invoice " + xx]
    }
}