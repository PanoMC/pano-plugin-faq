<div class="container vstack gap-3">
  <!-- Action Menu -->
  <PageActions leftClasses="d-lg-flex d-none">
    <CardMenu slot="middle">
      <CardMenuItem href="/faq" matchingList={['/faq']}>
        {$_('faq.list')}
      </CardMenuItem>
      <CardMenuItem href="/faq/categories" matchingList={['/faq/categories']}>
        {$_('faq.categories')}
      </CardMenuItem>
    </CardMenu>
    <div slot="right">
      <button type="button" class="btn btn-secondary" on:click={openAddFAQ}>
        <i class="fas fa-plus"></i>
        <span class="d-lg-inline d-none ms-2">{$_('faq.add_faq')}</span>
      </button>
    </div>
  </PageActions>

  <div class="card">
    <CardHeader>
      <div slot="left">
        {faqCount} Soru
      </div>
      <div slot="middle" style="width: 250px;">
        <div class="input-group">
          <input
            type="text"
            class="form-control form-control-sm {String(searchQuery || '').trim()
              ? 'border-secondary'
              : ''}"
            bind:value={searchQuery}
            on:input={(e) => onSearchInput(e.target.value)}
            placeholder="Bul..." />
          {#if searching}
            <span class="input-group-text">
              <span class="spinner-border spinner-border-sm text-secondary" role="status"></span>
            </span>
          {/if}
        </div>
      </div>
      <CardFilters slot="right">
        <CardFiltersItem href={`/faq`} active={currentStatus === 'ALL'}>
          {$_('all')}
        </CardFiltersItem>
        <CardFiltersItem href={`/faq?status=ACTIVE`} active={currentStatus === 'ACTIVE'}>
          {$_('active')}
        </CardFiltersItem>
        <CardFiltersItem href={`/faq?status=INACTIVE`} active={currentStatus === 'INACTIVE'}>
          {$_('inactive')}
        </CardFiltersItem>
      </CardFilters>
    </CardHeader>

    {#if paginatedItems.length === 0}
      <NoContent />
    {:else}
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
            <tr>
              <th scope="col" class="align-middle text-center" style="width: 60px;"></th>
              <!-- Action Column -->
              <th scope="col" class="align-middle text-center" style="width: 60px;">ID</th>
              <th scope="col" class="align-middle">{$_('faq.question')}</th>
              <th scope="col" class="align-middle">{$_('faq.category')}</th>
              <th scope="col" class="align-middle">{$_('faq.active')}</th>
              <th scope="col" class="align-middle text-center">{$_('faq.display_order')}</th>
            </tr>
          </thead>
          <tbody>
            {#each paginatedItems as faq (faq.id)}
              <FAQRow {faq} onEdit={openEditFAQ} onDelete={openDeleteFAQ} />
            {/each}
          </tbody>
        </table>
      </div>
      <div class="card-footer">
        <Pagination page={data.page} {totalPage} on:change={onPageChange} />
      </div>
    {/if}
  </div>

  <!-- Modals -->
  <AddEditFAQModal />
  <ConfirmDeleteFAQModal />
</div>

<script context="module">
  import ApiUtil, { buildQueryParams } from '@panomc/sdk/utils/api';

  export async function load(event) {
    const {
      parent,
      url: { searchParams },
    } = event;
    const { pageTitle } = await parent();
    pageTitle.set('plugins.pano-plugin-faq.faq.title');

    const page = searchParams.get('page') || 1;
    const statusParam = searchParams.get('status');
    const search = searchParams.get('search');

    const queryParams = buildQueryParams({
      page,
      status: statusParam,
      search,
    });

    try {
      const res = await ApiUtil.get({
        path: '/api/panel/faq/list' + queryParams,
        request: event,
      });

      return {
        data: res,
      };
    } catch (e) {
      console.error('[FAQ] Failed to load data', e);
      return {
        data: { faqs: [], categories: [], faqCount: 0, totalPage: 1, page: 1 },
      };
    }
  }
</script>

<script>
  import { base, page, goto } from '@panomc/sdk/svelte';
  import {
    PageActions,
    CardHeader,
    CardFilters,
    CardFiltersItem,
    NoContent,
    Pagination,
    CardMenu,
    CardMenuItem,
  } from '@panomc/sdk/components/panel';
  import { _ } from '../../main';
  import { buildQueryParams as createQueryParams } from '@panomc/sdk/utils/api';

  import FAQRow from '../components/FAQRow.svelte';
  import AddEditFAQModal, {
    show as showAddEditFAQModal,
    setCallback as setAddEditFAQCallback,
  } from '../components/modals/AddEditFAQModal.svelte';
  import ConfirmDeleteFAQModal, {
    show as showDeleteFAQModal,
    setCallback as setDeleteFAQCallback,
  } from '../components/modals/ConfirmDeleteFAQModal.svelte';

  export let data;

  $: ({ faqs, categories, faqCount, totalPage } = data);
  $: currentStatus = $page.url.searchParams.get('status') || 'ALL';
  $: searchQuery = $page.url.searchParams.get('search') || '';

  let searchTimeout;
  let searching = false;

  async function refreshData() {
    searching = true;
    const pageNum = data.page === 1 ? null : data.page;
    const statusVal = $page.url.searchParams.get('status');
    const searchVal = searchQuery || null;

    const queryParams = createQueryParams({
      page: pageNum,
      status: statusVal,
      search: searchVal,
    });

    try {
      await goto(base + `/faq${queryParams}`, {
        invalidateAll: true,
        keepfocus: true,
        noscroll: true,
      });
    } finally {
      searching = false;
    }
  }

  function onSearchInput(value) {
    searchQuery = value;
    data.page = 1;
    refreshData();
  }

  setAddEditFAQCallback(refreshData);
  setDeleteFAQCallback(refreshData);

  function openAddFAQ() {
    showAddEditFAQModal('create', null, categories);
  }

  function openEditFAQ(faq) {
    showAddEditFAQModal('edit', faq, categories);
  }

  function openDeleteFAQ(faq) {
    showDeleteFAQModal(faq);
  }

  // List with category names
  $: flatList = faqs.map((f) => {
    const cat = categories.find((c) => c.id === f.categoryId);
    return { ...f, categoryName: cat ? cat.name : $_('faq.uncategorized') };
  });

  $: paginatedItems = flatList; // Data is already paginated by server

  async function onPageChange(e) {
    data.page = e.detail;
    await refreshData();
  }
</script>
