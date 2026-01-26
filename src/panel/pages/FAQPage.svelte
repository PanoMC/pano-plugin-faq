<script context="module">
    import ApiUtil from '@panomc/sdk/utils/api';

    export async function load(event) {
        const { parent } = event;
        const { pageTitle } = await parent();
        pageTitle.set('plugins.pano-plugin-faq.faq.title');

        try {
            const res = await ApiUtil.get({
                path: '/api/panel/faq/list',
                request: event
            });
            
            return {
                data: res
            };
        } catch (e) {
            console.error('[FAQ] Failed to load data', e);
            return {
                data: { faqs: [], categories: [] }
            };
        }
    }
</script>

<script>
    import { base, goto } from '@panomc/sdk/svelte';
    import { PageActions, CardHeader, CardFilters, CardFiltersItem, NoContent, Pagination } from '@panomc/sdk/components/panel';
    import { _ } from '../../main';
    
    import FAQRow from '../components/FAQRow.svelte';
    import AddEditFAQModal, { show as showAddEditFAQModal, setCallback as setAddEditFAQCallback } from '../components/modals/AddEditFAQModal.svelte';
    import AddEditFAQCategoryModal, { show as showAddEditCategoryModal, setCallback as setAddEditCategoryCallback } from '../components/modals/AddEditFAQCategoryModal.svelte';
    import ConfirmDeleteFAQModal, { show as showDeleteFAQModal, setCallback as setDeleteFAQCallback } from '../components/modals/ConfirmDeleteFAQModal.svelte';
    import ConfirmDeleteFAQCategoryModal, { show as showDeleteCategoryModal, setCallback as setDeleteCategoryCallback } from '../components/modals/ConfirmDeleteFAQCategoryModal.svelte';

    export let data;
    
    let { faqs, categories } = data;
    $: ({ faqs, categories } = data);

    let searchQuery = '';
    let currentPage = 1;
    let itemsPerPage = 10;
    let currentStatus = 'ALL'; // ALL, ACTIVE, INACTIVE

    async function refreshData() {
        await goto(`${base}/faq`, { invalidateAll: true });
    }

    setAddEditFAQCallback(refreshData);
    setAddEditCategoryCallback(refreshData);
    setDeleteFAQCallback(refreshData);
    setDeleteCategoryCallback(refreshData);

    function openAddFAQ() {
        showAddEditFAQModal('create', null, categories);
    }

    function openEditFAQ(faq) {
        showAddEditFAQModal('edit', faq, categories);
    }

    function openDeleteFAQ(faq) {
        showDeleteFAQModal(faq);
    }

    function openAddCategory() {
        showAddEditCategoryModal('create', null);
    }

    // Filter Logic
    $: filteredFAQs = faqs.filter(faq => {
        const matchesSearch = 
            faq.question.toLowerCase().includes(searchQuery.toLowerCase()) || 
            faq.answer.toLowerCase().includes(searchQuery.toLowerCase());
        
        const matchesStatus = 
            currentStatus === 'ALL' || 
            (currentStatus === 'ACTIVE' && faq.isActive) || 
            (currentStatus === 'INACTIVE' && !faq.isActive);

        return matchesSearch && matchesStatus;
    });

    $: flatList = filteredFAQs.map(f => {
        const cat = categories.find(c => c.id === f.categoryId);
        return { ...f, categoryName: cat ? cat.name : $_('faq.uncategorized') };
    }).sort((a,b) => (a.categoryId || 0) - (b.categoryId || 0) || a.displayOrder - b.displayOrder);

    $: totalItems = flatList.length;
    $: totalPage = Math.ceil(totalItems / itemsPerPage) || 1;
    $: paginatedItems = flatList.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    function onPageChange(e) {
        currentPage = e.detail;
    }
</script>

<article class="container vstack gap-3">
    <!-- Action Menu -->
    <PageActions>
        <div slot="left" class="d-none d-lg-block">
             <div class="input-group input-group-sm" style="width: 250px;">
                <span class="input-group-text bg-white border-end-0">
                    <i class="fas fa-search text-muted"></i>
                </span>
                <input type="search" class="form-control border-start-0 ps-0" bind:value={searchQuery} placeholder={$_('faq.search')} />
            </div>
        </div>
        <div slot="right" class="d-flex gap-2">
            <button type="button" class="btn btn-secondary" on:click={openAddCategory}>
                <i class="fas fa-folder-plus"></i>
                <span class="d-lg-inline d-none ms-2">{$_('faq.add_category')}</span>
            </button>
            <button type="button" class="btn btn-primary" on:click={openAddFAQ}>
                <i class="fas fa-plus"></i>
                <span class="d-lg-inline d-none ms-2">{$_('faq.add_faq')}</span>
            </button>
        </div>
    </PageActions>

    <div class="card">
        <CardHeader>
            <div slot="left">
                {$_('faq.list')} ({totalItems})
            </div>
            <CardFilters slot="right">
                <button class="nav-link border-0 bg-transparent" class:active={currentStatus === 'ALL'} on:click={() => currentStatus = 'ALL'}>
                    {$_('all')}
                </button>
                <button class="nav-link border-0 bg-transparent" class:active={currentStatus === 'ACTIVE'} on:click={() => currentStatus = 'ACTIVE'}>
                    {$_('active')}
                </button>
                <button class="nav-link border-0 bg-transparent" class:active={currentStatus === 'INACTIVE'} on:click={() => currentStatus = 'INACTIVE'}>
                    {$_('inactive')}
                </button>
            </CardFilters>
        </CardHeader>

        {#if paginatedItems.length === 0}
             <NoContent />
        {:else}
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                 <thead>
                    <tr>
                        <th scope="col" class="align-middle text-center" style="width: 60px;"></th> <!-- Action Column -->
                        <th scope="col" class="align-middle text-center" style="width: 60px;">ID</th>
                        <th scope="col" class="align-middle">{$_('faq.question')}</th>
                        <th scope="col" class="align-middle">{$_('faq.category')}</th>
                        <th scope="col" class="align-middle">{$_('faq.active')}</th>
                        <th scope="col" class="align-middle text-center">{$_('faq.display_order')}</th>
                    </tr>
                </thead>
                <tbody>
                     {#each paginatedItems as faq (faq.id)}
                        <FAQRow 
                            {faq} 
                            onEdit={openEditFAQ} 
                            onDelete={openDeleteFAQ} 
                        />
                    {/each}
                </tbody>
            </table>
        </div>
        <div class="card-footer">
            <Pagination 
                page={currentPage} 
                totalPage={totalPage} 
                on:change={onPageChange} 
            />
        </div>
        {/if}
    </div>

    <!-- Modals -->
    <AddEditFAQModal />
    <AddEditFAQCategoryModal />
    <ConfirmDeleteFAQModal />
    <ConfirmDeleteFAQCategoryModal />
</article>
