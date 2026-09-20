package com.mypum.pos.domain.model.subscription

data class PlanEntitlements(
    val maxProducts: Int?,
    val advancedReports: Boolean,
    val exportData: Boolean,
    val bulkImport: Boolean,
    val backupAdvanced: Boolean,
    val employeeManagement: Boolean,
    val cloudSync: Boolean,
    val multiStore: Boolean
) {
    val unlimitedProducts: Boolean
        get() = maxProducts == null

    companion object {

        val FREE = PlanEntitlements(
            maxProducts = 50,
            advancedReports = false,
            exportData = false,
            bulkImport = false,
            backupAdvanced = false,
            employeeManagement = false,
            cloudSync = false,
            multiStore = false
        )

        val PRO = PlanEntitlements(
            maxProducts = null,
            advancedReports = true,
            exportData = true,
            bulkImport = true,
            backupAdvanced = true,
            employeeManagement = true,
            cloudSync = true,
            multiStore = true
        )

        fun forPlan(plan: Plan): PlanEntitlements =
            when (plan) {
                Plan.FREE -> FREE
                Plan.PRO -> PRO
            }
    }
}
