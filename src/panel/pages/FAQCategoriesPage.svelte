<script context="module">
    import ApiUtil, {buildQueryParams} from '@panomc/sdk/utils/api';

    export async function load(event) {
        const { parent, url: { searchParams } } = event;
        const { pageTitle } = await parent();
        pageTitle.set('plugins.pano-plugin-faq.faq.categories');

        const page = searchParams.get('page') || 1;
        const search = searchParams.get('search');

        const queryParams = buildQueryParams({
            page,
            search
        });

        try {
            const res = await ApiUtil.get({
                path: '/api/panel/faq/category/list' + queryParams,
                request: event
            });
            
            return {
                data: res
            };
        } catch (e) {
            console.error('[FAQ] Failed to load data', e);
            return {
                data: { categories: [], categoryCount: 0, totalPage: 1, page: 1 }
            };
        }
    }
</script>

<script>
    import { base, page, goto } from '@panomc/sdk/svelte';
    import { PageActions, CardHeader, NoContent, Pagination } from '@panomc/sdk/components/panel';
    import { _ } from '../../main';
    import { buildQueryParams as createQueryParams } from '@panomc/sdk/utils/api';
    
    import FAQCategoryRow from '../components/FAQCategoryRow.svelte';
    import AddEditFAQCategoryModal, { show as showAddEditCategoryModal, setCallback as setAddEditCategoryCallback } from '../components/modals/AddEditFAQCategoryModal.svelte';
    import ConfirmDeleteFAQCategoryModal, { show as showDeleteCategoryModal, setCallback as setDeleteCategoryCallback } from '../components/modals/ConfirmDeleteFAQCategoryModal.svelte';

    export let data;
    
    $: ({ categories, categoryCount, totalPage } = data);
    $: searchQuery = $page.url.searchParams.get('search') || '';

    let searchTimeout;
    let searching = false;

    async function refreshData() {
        const pageNum = data.page === 1 ? null : data.page;
        const searchVal = searchQuery || null;

        const queryParams = createQueryParams({
            page: pageNum,
            search: searchVal
        });

        await goto(base + `/faq/categories${queryParams}`, { invalidateAll: true, keepfocus: true, noscroll: true });
    }

    function handleSearch() {
        searching = true;
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(async () => {
            data.page = 1; // Reset to page 1 on search
            try {
                await refreshData();
            } finally {
                searching = false;
            }
        }, 500);
    }

    setAddEditCategoryCallback(refreshData);
    setDeleteCategoryCallback(refreshData);

    function openAddCategory() {
        showAddEditCategoryModal('create', null);
    }

    function openEditCategory(category) {
        showAddEditCategoryModal('edit', category);
    }

    function openDeleteCategory(category) {
        showDeleteCategoryModal(category);
    }

    function onPageChange(e) {
        data.page = e.detail;
        refreshData();
    }
</script>

<article class="container vstack gap-3">
    <!-- Action Menu -->
    <PageActions>
        <div slot="left" class="d-flex gap-2">
            <a href={`${base}/faq`} class="link-secondary text-decoration-none align-self-center me-3">
                <i class="fas fa-arrow-left me-2"></i>
                {$_('faq.list')}
            </a>
            <div class="input-group input-group-sm d-none d-lg-flex border rounded overflow-hidden" style="width: 250px;">
                <span class="input-group-text bg-body-tertiary border-0 pe-1">
                    {#if searching}
                        <span class="spinner-border spinner-border-sm text-secondary" role="status"></span>
                    {:else}
                        <i class="fas fa-search text-secondary"></i>
                    {/if}
                </span>
                <input type="search" class="form-control border-0 bg-body-tertiary shadow-none ps-2" bind:value={searchQuery} on:input={handleSearch} placeholder={$_('faq.search_category')} />
                {#if searchQuery}
                    <button type="button" class="btn btn-link link-secondary border-0 bg-body-tertiary px-2 py-0 text-decoration-none" on:click={() => { searchQuery = ''; handleSearch(); }}>
                        <i class="fas fa-times"></i>
                    </button>
                {/if}
            </div>
        </div>
        <div slot="right" class="d-flex gap-2">
            <button type="button" class="btn btn-primary" on:click={openAddCategory}>
                <i class="fas fa-plus"></i>
                <span class="d-lg-inline d-none ms-2">{$_('faq.add_category')}</span>
            </button>
        </div>
    </PageActions>

    <div class="card">
        <CardHeader>
            <div slot="left">
                {$_('faq.categories')} ({categoryCount})
            </div>
        </CardHeader>

        {#if categories.length === 0}
             <NoContent />
        {:else}
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                 <thead>
                    <tr>
                        <th scope="col" class="align-middle text-center" style="width: 60px;"></th> <!-- Action Column -->
                        <th scope="col" class="align-middle text-center" style="width: 60px;">ID</th>
                        <th scope="col" class="align-middle">{$_('faq.category_modal.name')}</th>
                        <th scope="col" class="align-middle text-center" style="width: 150px;">{$_('faq.display_order')}</th>
                    </tr>
                </thead>
                <tbody>
                     {#each categories as category (category.id)}
                        <FAQCategoryRow 
                            {category} 
                            onEdit={openEditCategory} 
                            onDelete={openDeleteCategory} 
                        />
                    {/each}
                </tbody>
            </table>
        </div>
        <div class="card-footer">
            <Pagination 
                page={data.page} 
                totalPage={totalPage} 
                on:change={onPageChange} 
            />
        </div>
        {/if}
    </div>

    <!-- Modals -->
    <AddEditFAQCategoryModal />
    <ConfirmDeleteFAQCategoryModal />
</article>
